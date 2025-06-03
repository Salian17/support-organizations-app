package com.example.supportorganizationsapp.controllers;

import com.example.supportorganizationsapp.config.JwtConstants;
import com.example.supportorganizationsapp.config.TokenProvider;
import com.example.supportorganizationsapp.dto.request.auth.LoginRequestDTO;
import com.example.supportorganizationsapp.dto.request.auth.RefreshTokenRequestDTO;
import com.example.supportorganizationsapp.dto.request.auth.SignUpRequest;
import com.example.supportorganizationsapp.dto.response.auth.LoginResponseDTO;
import com.example.supportorganizationsapp.enums.RoleEnum;
import com.example.supportorganizationsapp.exception.UserException;
import com.example.supportorganizationsapp.models.User;
import com.example.supportorganizationsapp.repository.UserRepository;
import com.example.supportorganizationsapp.service.implementation.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(
            summary = "Регистрация нового сопровождающего",
            description = "Создаёт нового сопровождающего",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Регистрация успешно инициирована"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные или сопровождающего уже существует")
            }
    )
    @PostMapping("/signup/companion")
    public ResponseEntity<LoginResponseDTO> signupCompanion(
            @Parameter(description = "Данные для регистрации", required = true)
            @RequestBody SignUpRequest signupRequestDTO) throws UserException {

        final String email = signupRequestDTO.getEmail();
        final String phoneNum = signupRequestDTO.getPhoneNumber();
        final String password = signupRequestDTO.getPassword();
        final String firstName = signupRequestDTO.getFirstName();
        final String lastName = signupRequestDTO.getLastName();

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new UserException("Account with email " + email + " already exists");
        }

        User newUser = User.builder()
                .email(email)
                .phoneNumber(phoneNum)
                .firstName(firstName)
                .lastName(lastName)
                .roleEnum(RoleEnum.COMPANION)
                .password(passwordEncoder.encode(password))
                .build();

        User savedUser = userRepository.save(newUser);

        // Загружаем UserDetails для получения ролей
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем оба токена
        String accessToken = tokenProvider.generateToken(authentication, JwtConstants.ACCESS_TOKEN_VALIDITY);
        String refreshToken = tokenProvider.generateToken(authentication, JwtConstants.REFRESH_TOKEN_VALIDITY);

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getId())
                .isAuthenticated(true)
                .build();

        log.info("User {} successfully signed up with ID {}", email, savedUser.getId());

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Регистрация успешно инициирована"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные или пользователь уже существует")
            }
    )
    @PostMapping("/signup/passenger")
    public ResponseEntity<LoginResponseDTO> signup(
            @Parameter(description = "Данные для регистрации", required = true)
            @RequestBody SignUpRequest signupRequestDTO) throws UserException {

        final String email = signupRequestDTO.getEmail();
        final String phoneNum = signupRequestDTO.getPhoneNumber();
        final String password = signupRequestDTO.getPassword();
        final String firstName = signupRequestDTO.getFirstName();
        final String lastName = signupRequestDTO.getLastName();

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new UserException("Account with email " + email + " already exists");
        }

        User newUser = User.builder()
                .email(email)
                .phoneNumber(phoneNum)
                .firstName(firstName)
                .lastName(lastName)
                .roleEnum(RoleEnum.PASSENGER)
                .password(passwordEncoder.encode(password))
                .build();

        User savedUser = userRepository.save(newUser);

        // Загружаем UserDetails для получения ролей
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем оба токена
        String accessToken = tokenProvider.generateToken(authentication, JwtConstants.ACCESS_TOKEN_VALIDITY);
        String refreshToken = tokenProvider.generateToken(authentication, JwtConstants.REFRESH_TOKEN_VALIDITY);

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getId())
                .isAuthenticated(true)
                .build();

        log.info("User {} successfully signed up with ID {}", email, savedUser.getId());

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Проверяет учетные данные и возвращает JWT access и refresh токены, а также ID пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно"),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "Данные для входа", required = true)
            @RequestBody LoginRequestDTO loginRequestDTO) {

        final String email = loginRequestDTO.getEmail();
        final String password = loginRequestDTO.getPassword();

        Authentication authentication = authenticateReq(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Получаем ID пользователя из базы данных
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Генерируем оба токена
        String accessToken = tokenProvider.generateToken(authentication, JwtConstants.ACCESS_TOKEN_VALIDITY);
        String refreshToken = tokenProvider.generateToken(authentication, JwtConstants.REFRESH_TOKEN_VALIDITY);

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .isAuthenticated(true)
                .build();

        log.info("User {} successfully signed in with ID {}", email, user.getId());

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Обновление access токена",
            description = "Получает новый access токен на основе переданного refresh токена.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токен успешно обновлен"),
                    @ApiResponse(responseCode = "401", description = "Недействительный refresh токен")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @Parameter(description = "Refresh токен", required = true)
            @RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        try {
            Claims claims = tokenProvider.validateToken(refreshToken);
            String email = claims.get(JwtConstants.EMAIL, String.class);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = tokenProvider.generateToken(authentication, JwtConstants.ACCESS_TOKEN_VALIDITY);
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .isAuthenticated(true)
                    .build();
            log.info("Access token refreshed for user: {}", email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication authenticateReq(String username, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
