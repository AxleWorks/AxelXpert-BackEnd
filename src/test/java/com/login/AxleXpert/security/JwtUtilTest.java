package com.login.AxleXpert.security;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.login.AxleXpert.Branches.entity.Branch;
import com.login.AxleXpert.Users.entity.User;
import com.login.AxleXpert.testutils.TestDataBuilder;

/**
 * Unit Tests for JwtUtil
 * 
 * Learning Focus:
 * - Testing JWT token generation and validation
 * - Testing claims extraction (username, email, role, id, branchId)
 * - Testing token expiration logic
 * - Using ReflectionTestUtils to inject secret key
 */
@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;
    private static final String TEST_SECRET = "test-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        // Inject the secret key using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "secretKey", TEST_SECRET);
        
        // Create test user with branch
        Branch testBranch = TestDataBuilder.createBranch();
        testBranch.setId(10L);
        
        testUser = TestDataBuilder.createUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole("CUSTOMER");
        testUser.setBranch(testBranch);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid JWT token")
        void shouldGenerateValidToken() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            
            // ASSERT
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
        }

        @Test
        @DisplayName("Should include user ID in token claims")
        void shouldIncludeUserId() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            Long extractedId = jwtUtil.extractId(token);
            
            // ASSERT
            assertThat(extractedId).isEqualTo(testUser.getId());
        }

        @Test
        @DisplayName("Should include username in token subject")
        void shouldIncludeUsername() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            String extractedUsername = jwtUtil.extractUsername(token);
            
            // ASSERT
            assertThat(extractedUsername).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("Should include email in token claims")
        void shouldIncludeEmail() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            String extractedEmail = jwtUtil.extractEmail(token);
            
            // ASSERT
            assertThat(extractedEmail).isEqualTo(testUser.getEmail());
        }

        @Test
        @DisplayName("Should include role in token claims")
        void shouldIncludeRole() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            String extractedRole = jwtUtil.extractRole(token);
            
            // ASSERT
            assertThat(extractedRole).isEqualTo(testUser.getRole());
        }

        @Test
        @DisplayName("Should include branch ID when user has branch")
        void shouldIncludeBranchId() {
            // ACT
            String token = jwtUtil.generateToken(testUser);
            Long extractedBranchId = jwtUtil.extractBranchId(token);
            
            // ASSERT
            assertThat(extractedBranchId).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should handle null branch ID")
        void shouldHandleNullBranch() {
            // ARRANGE
            testUser.setBranch(null);
            
            // ACT
            String token = jwtUtil.generateToken(testUser);
            Long extractedBranchId = jwtUtil.extractBranchId(token);
            
            // ASSERT
            assertThat(extractedBranchId).isNull();
        }

        @Test
        @DisplayName("Should set expiration to 10 hours from now")
        void shouldSetCorrectExpiration() {
            // ARRANGE
            long beforeGeneration = System.currentTimeMillis();
            
            // ACT
            String token = jwtUtil.generateToken(testUser);
            Date expiration = jwtUtil.extractExpiration(token);
            
            // ASSERT
            long expectedExpiration = beforeGeneration + (1000 * 60 * 60 * 10); // 10 hours
            long actualExpiration = expiration.getTime();
            
            // Allow 1 second tolerance for execution time
            assertThat(actualExpiration).isBetween(
                expectedExpiration - 1000,
                expectedExpiration + 1000
            );
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate token successfully")
        void shouldValidateTokenSuccessfully() {
            // ARRANGE
            String token = jwtUtil.generateToken(testUser);
            
            // ACT
            Boolean isValid = jwtUtil.validateToken(token, testUser.getUsername());
            
            // ASSERT
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should fail validation with wrong username")
        void shouldFailValidation_withWrongUsername() {
            // ARRANGE
            String token = jwtUtil.generateToken(testUser);
            
            // ACT
            Boolean isValid = jwtUtil.validateToken(token, "wrongusername");
            
            // ASSERT
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should fail validation for expired token")
        void shouldFailValidation_whenTokenExpired() throws InterruptedException {
            // ARRANGE - Create an expired token
            String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(testUser.getUsername())
                .claim("id", testUser.getId())
                .claim("email", testUser.getEmail())
                .claim("role", testUser.getRole())
                .claim("branchId", testUser.getBranch() != null ? testUser.getBranch().getId() : null)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 11)) // 11 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago (expired)
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .compact();
            
            // ACT & ASSERT
            // Expired token will throw exception during extraction, which causes validation to fail
            assertThatThrownBy(() -> jwtUtil.validateToken(expiredToken, testUser.getUsername()))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
        }
    }

    @Nested
    @DisplayName("Claims Extraction Tests")
    class ClaimsExtractionTests {

        private String validToken;

        @BeforeEach
        void setUpToken() {
            validToken = jwtUtil.generateToken(testUser);
        }

        @Test
        @DisplayName("Should extract username correctly")
        void shouldExtractUsername() {
            // ACT
            String username = jwtUtil.extractUsername(validToken);
            
            // ASSERT
            assertThat(username).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should extract user ID correctly")
        void shouldExtractId() {
            // ACT
            Long id = jwtUtil.extractId(validToken);
            
            // ASSERT
            assertThat(id).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should extract email correctly")
        void shouldExtractEmail() {
            // ACT
            String email = jwtUtil.extractEmail(validToken);
            
            // ASSERT
            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should extract role correctly")
        void shouldExtractRole() {
            // ACT
            String role = jwtUtil.extractRole(validToken);
            
            // ASSERT
            assertThat(role).isEqualTo("CUSTOMER");
        }

        @Test
        @DisplayName("Should extract branch ID correctly")
        void shouldExtractBranchId() {
            // ACT
            Long branchId = jwtUtil.extractBranchId(validToken);
            
            // ASSERT
            assertThat(branchId).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should extract expiration date correctly")
        void shouldExtractExpiration() {
            // ACT
            Date expiration = jwtUtil.extractExpiration(validToken);
            
            // ASSERT
            assertThat(expiration).isNotNull();
            assertThat(expiration).isAfter(new Date()); // Should be in the future
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle user with different roles")
        void shouldHandleDifferentRoles() {
            // ARRANGE
            testUser.setRole("ADMIN");
            
            // ACT
            String token = jwtUtil.generateToken(testUser);
            String extractedRole = jwtUtil.extractRole(token);
            
            // ASSERT
            assertThat(extractedRole).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void shouldGenerateDifferentTokens() {
            // ARRANGE
            User user2 = TestDataBuilder.createUser();
            user2.setId(2L);
            user2.setUsername("user2");
            user2.setEmail("user2@example.com");
            
            // ACT
            String token1 = jwtUtil.generateToken(testUser);
            String token2 = jwtUtil.generateToken(user2);
            
            // ASSERT
            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("Should generate different tokens at different times")
        void shouldGenerateDifferentTokensAtDifferentTimes() throws InterruptedException {
            // ACT
            String token1 = jwtUtil.generateToken(testUser);
            Thread.sleep(1100); // Wait 1.1 seconds (JWT uses seconds precision)
            String token2 = jwtUtil.generateToken(testUser);
            
            // ASSERT
            assertThat(token1).isNotEqualTo(token2); // Different issued-at times
            
            // Verify they have different issuedAt timestamps
            Date iat1 = jwtUtil.extractClaim(token1, io.jsonwebtoken.Claims::getIssuedAt);
            Date iat2 = jwtUtil.extractClaim(token2, io.jsonwebtoken.Claims::getIssuedAt);
            assertThat(iat2).isAfter(iat1);
        }

        @Test
        @DisplayName("Should throw exception for invalid token format")
        void shouldThrowException_forInvalidToken() {
            // ACT & ASSERT
            assertThatThrownBy(() -> jwtUtil.extractUsername("invalid.token.format"))
                .isInstanceOf(Exception.class); // JWT parsing exception
        }

        @Test
        @DisplayName("Should throw exception for tampered token")
        void shouldThrowException_forTamperedToken() {
            // ARRANGE
            String token = jwtUtil.generateToken(testUser);
            String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX"; // Tamper with signature
            
            // ACT & ASSERT
            assertThatThrownBy(() -> jwtUtil.extractUsername(tamperedToken))
                .isInstanceOf(Exception.class); // Signature validation fails
        }
    }
}
