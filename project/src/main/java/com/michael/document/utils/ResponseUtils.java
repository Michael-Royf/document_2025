package com.michael.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michael.document.domain.response.Response;
import com.michael.document.exception.ApiException;
import com.michael.document.exception.ExistException;
import com.michael.document.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ResponseUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse = ((httpServletResponse, response) -> {
        try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    });

    private static final BiFunction<Exception, HttpStatus, String> errorReason = ((exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(FORBIDDEN)) {
            return "You don't have permission to access this resource.";
        }
        if (httpStatus.isSameCodeAs(UNAUTHORIZED)) {
            return "You are not authorized to access this resource.";
        }
        if (exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof CredentialsExpiredException ||
                exception instanceof ApiException ||
                exception instanceof ExistException ||
                exception instanceof NotFoundException) {
            return exception.getMessage();
        }
        if (httpStatus.is5xxServerError()) {
            return "Internal Server Error";
        } else {
            return "An error occurred. Please try again";
        }
    });


    public static Response getResponse(HttpServletRequest request,
                                       Map<?, ?> data,
                                       String message,
                                       HttpStatus status) {
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data
        );
    }

    public static void handleErrorResponse(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Exception exception) {
        if (exception instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, exception, FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }

    }

    private static Response getErrorResponse(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Exception exception,
                                             HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return Response.builder()
                .time(LocalDateTime.now().toString())
                .code(status.value())
                .path(request.getRequestURI())
                .status(HttpStatus.valueOf(status.value()))
                .message(errorReason.apply(exception, status))
                .exception(getRootCauseMessage(exception))
                .data(emptyMap())
                .build();
    }


}
