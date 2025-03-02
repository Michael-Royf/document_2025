package com.michael.document.security;

import com.michael.document.services.JwtService;
import com.michael.document.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiHttpConfigurer extends AbstractHttpConfigurer<ApiHttpConfigurer, HttpSecurity> {

    private final ApiAuthorizationFilter authorizationFilter;
    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;


    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(apiAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new ApiAuthenticationFilter(
                        authenticationConfiguration.getAuthenticationManager(),
                        userService,
                        jwtService),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
/*
 Этот класс определяет конфигуратор безопасности в Spring Security,
 который настраивает определённые фильтры и поставщиков аутентификации для вашего приложения.
 Конфигуратор наследует от AbstractHttpConfigurer,
 что позволяет вам настраивать параметры безопасности для вашего приложения.

 Метод init(HttpSecurity http): В этом методе вызывается http.authenticationProvider(apiAuthenticationProvider),
 который добавляет в цепочку аутентификации кастомный провайдер аутентификации apiAuthenticationProvider.
 Это значит, что Spring Security будет использовать данный провайдер для аутентификации пользователей.

 Метод configure(HttpSecurity http): Здесь настраиваются фильтры:

 http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class):
 Этот метод добавляет фильтр authorizationFilter перед стандартным фильтром UsernamePasswordAuthenticationFilter.
 Это означает, что фильтр авторизации будет проверять запросы до того, как будет выполнена аутентификация по имени пользователя и паролю.

http.addFilterAfter(new ApiAuthenticationFilter(...), UsernamePasswordAuthenticationFilter.class):
Здесь добавляется фильтр ApiAuthenticationFilter после фильтра UsernamePasswordAuthenticationFilter.
Этот фильтр, скорее всего, используется для аутентификации на основе других данных (например, JWT-токена) после того,
как была выполнена базовая аутентификация с именем пользователя и паролем.


Конфигуратор ApiHttpConfigurer настраивает цепочку фильтров и аутентификацию для вашего приложения.
Он делает следующее:
Использует apiAuthenticationProvider для аутентификации.
Добавляет фильтр авторизации (authorizationFilter), который, вероятно, проверяет,
авторизован ли пользователь, например, по токену, перед выполнением стандартной аутентификации.
Добавляет фильтр аутентификации (ApiAuthenticationFilter), который,
возможно, занимается проверкой JWT или других токенов после выполнения стандартной аутентификации.

UsernamePasswordAuthenticationFilter — это стандартный фильтр Spring Security, который отвечает за аутентификацию пользователей по имени и паролю.
 Он перехватывает запросы, пытаясь выполнить аутентификацию.
 Резюме:
ApiHttpConfigurer — это класс, который настраивает фильтры и аутентификацию для защиты API.
Он использует кастомные фильтры и поставщика аутентификации для выполнения аутентификации с использованием JWT и других методов.
Этот конфигуратор позволяет вам настроить порядок выполнения фильтров и аутентификацию, обеспечивая гибкость и контроль над процессом безопасности.
 */