package com.titan.dispatch.repository;

import com.titan.dispatch.TitanDispatchApplication;
import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.DispatchStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
// Tells Spring Boot: "Do NOT replace my Testcontainer with an in-memory H2 database"
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = TitanDispatchApplication.class)
class DispatchAllocationRepositoryTest {

    // Spins up the EXACT PostGIS image used in our docker-compose!
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            org.testcontainers.utility.DockerImageName.parse("postgis/postgis:15-3.3").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("titan_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private DispatchAllocationRepository dispatchAllocationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private OperatorRepository operatorRepository;

    @Test
    @DisplayName("Should save Dispatch Allocation and verify @SoftDelete functionality")
    void testSaveAndSoftDelete() {
        // --- 1. ARRANGE: Create and save supporting entities ---

        // Adjust "assetTag" and "make" to match your actual Equipment fields
        Equipment equipment = new Equipment();
        equipment.setId(UUID.randomUUID());
        // equipment.setAssetTag("BULLDOZER-001");
        Equipment savedEquipment = equipmentRepository.save(equipment);

        // Adjust "firstName" and "licenseNumber" to match your actual Operator fields
        Operator operator = new Operator();
        operator.setId(UUID.randomUUID());
        // operator.setFirstName("John Doe");
        Operator savedOperator = operatorRepository.save(operator);

        // Create the main Dispatch Allocation
        DispatchAllocation allocation = DispatchAllocation.builder()
                .equipment(savedEquipment)
                .operator(savedOperator)
                .jobSiteId(UUID.randomUUID())
                .startDate(LocalDateTime.now())
                .requiresHeavyTransport(true)
                .status(DispatchStatus.PENDING)
                .build();

        DispatchAllocation savedAllocation = dispatchAllocationRepository.save(allocation);

        // --- 2. ACT: Soft delete the equipment ---
        equipmentRepository.delete(savedEquipment);
        equipmentRepository.flush(); // Force Hibernate to send the UPDATE deleted=true to DB
        dispatchAllocationRepository.flush();

        // --- 3. ASSERT: Verify the states ---

        // A. Verify the Dispatch was saved correctly
        assertThat(savedAllocation.getId()).isNotNull();

        // B. Verify @SoftDelete works: Equipment should no longer be findable by standard queries
        Optional<Equipment> deletedEquipment = equipmentRepository.findById(savedEquipment.getId());
        assertThat(deletedEquipment).isEmpty();

        // C. Verify the Dispatch is STILL findable, proving EAGER fetching doesn't crash on soft-deleted parents
        Optional<DispatchAllocation> retrievedAllocation = dispatchAllocationRepository.findById(savedAllocation.getId());
        assertThat(retrievedAllocation).isPresent();
    }
}