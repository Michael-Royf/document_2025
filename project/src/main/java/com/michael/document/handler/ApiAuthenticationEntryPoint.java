package com.michael.document.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.michael.document.utils.ResponseUtils.handleErrorResponse;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException, ServletException {
        handleErrorResponse(request, response, exception);
    }
}
/*
 Этот класс обрабатывает ошибки аутентификации для API.
 Основная задача такого компонента — обработка случаев, когда пользователь пытается получить доступ
 к защищённому ресурсу без необходимой аутентификации или с недействительными учетными данными.
 Метод commence вызывается, когда аутентификация пользователя не удалась. Это может происходить,
 если пользователь пытается обратиться к защищённому ресурсу без аутентификации.
 */