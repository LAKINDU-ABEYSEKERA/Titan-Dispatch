package com.titan.dispatch.web.controller;

import com.titan.dispatch.domain.entity.RefreshToken;
import com.titan.dispatch.domain.entity.SystemUser;
import com.titan.dispatch.infrastructure.security.JwtService;
import com.titan.dispatch.repository.RefreshTokenRepository;
import com.titan.dispatch.repository.SystemUserRepository;
import com.titan.dispatch.web.dto.AuthRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final SystemUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SystemUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String accessToken = jwtService.generateToken(userDetails);

        // Rotate Refresh Token
        refreshTokenRepository.deleteByUserId(user.getId());
        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshExpiration()))
                .build();
        refreshTokenRepository.save(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshTokenString)
                .httpOnly(true)
                .secure(true) // Requires HTTPS in production
                .path("/api/v1/auth/refresh")
                .maxAge(jwtService.getRefreshExpiration() / 1000)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("access_token", accessToken));
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request) {
        String refreshTokenString = null;
        if (request.getCookies() != null) {
            refreshTokenString = Arrays.stream(request.getCookies())
                    .filter(c -> "refresh_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (refreshTokenString == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        SystemUser user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("access_token", newAccessToken));
    }
}