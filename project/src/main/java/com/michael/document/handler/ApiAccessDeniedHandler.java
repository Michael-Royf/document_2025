package com.michael.document.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.michael.document.utils.ResponseUtils.handleErrorResponse;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        handleErrorResponse(request, response, exception);
    }
}
/*
 обрабатывает ситуацию, когда пользователь аутентифицирован,
 но у него нет прав для доступа к защищённому ресурсу.
 При возникновении ошибки доступа (например, если у пользователя нет прав на выполнение запроса),
 метод handle вызывается для отправки подробного ответа, с кодом ошибки 403.
 */