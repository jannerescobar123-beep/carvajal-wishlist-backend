package com.carvajal.wishlist.service;

import com.carvajal.wishlist.config.JwtUtil;
import com.carvajal.wishlist.dto.AuthResponseDTO;
import com.carvajal.wishlist.dto.UserDTO;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.exception.EmailAlreadyExistsException;
import com.carvajal.wishlist.exception.InvalidCredentialsException;
import com.carvajal.wishlist.exception.UsernameAlreadyExistsException;
import com.carvajal.wishlist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private UserDTO userDTO;
    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(null, "testuser", "test@example.com", "password", Role.CLIENT);
        user = new User("testuser", "test@example.com", "encodedPassword", Role.CLIENT);
        userDetails = org.springframework.security.core.userdetails.User.withUsername("testuser")
                .password("encodedPassword").roles("CLIENT").build();
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        AuthResponseDTO response = authService.register(userDTO);

        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(userDTO));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(userDTO));
    }

    @Test
    void testLogin_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        AuthResponseDTO response = authService.login("testuser", "password");

        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        assertThrows(InvalidCredentialsException.class, () -> authService.login("testuser", "wrongpassword"));
    }
}
