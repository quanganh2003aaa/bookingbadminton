package com.example.bookingbadminton.exception;

import com.example.bookingbadminton.model.dto.ErrorResponse;
import com.example.bookingbadminton.payload.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        log.warn("AppException: code={}, message={}", exception.getCode(), exception.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(exception.getCode());
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    ResponseEntity<ApiResponse> handlingResponseStatusException(ResponseStatusException exception) {
        log.warn("ResponseStatusException: status={}, reason={}", exception.getStatusCode(), exception.getReason(), exception);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(exception.getStatusCode().value());
        String message = exception.getReason();
        if (message == null || message.isBlank()) {
            message = exception.getStatusCode().toString();
        }
        apiResponse.setMessage(message);
        return ResponseEntity.status(exception.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Validation failed", exception);
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElseGet(() -> exception.getBindingResult().getAllErrors().stream()
                        .findFirst()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse("Validation failed"));

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse> handlingConstraintViolationException(ConstraintViolationException exception) {
        log.warn("Constraint violation", exception);
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .orElse("Validation failed");

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {
        log.error("Unhandled exception", exception);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiResponse.setMessage(exception.getMessage() != null ? exception.getMessage() : "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    /**
     * Handle exception when user not authenticated
     *
     * @param e
     * @param request
     * @return
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({InternalAuthenticationServiceException.class, AuthenticationException.class, UnauthorizedException.class})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Unauthorized",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "400 Response",
                                    summary = "Handle exception when authenticated failed",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 400,
                                              "path": "/api/v1/...",
                                              "error": "Bad request",
                                              "message": "Username or password is incorrect"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage("Email or password is incorrect");

        return errorResponse;
    }

    /*
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return
     * */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bad Request", content =
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples =
    @ExampleObject(name = "404 response",
            description = "Handle exception when resource not found",
            value = """
                                        {
                                            "timestamp": "2023-10-19T06:07:35.321+00:00",
                                            "status": 404,
                                            "path": "/api/v1/...",
                                            "error": "Not Found",
                                            "message": "{data} not found"
                                        }
                                        """
    )
    )
    )
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /**
     * Handle exception when the request not found data
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({ForBiddenException.class, AccessDeniedException.class, AccessDeniedException.class})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "403 Response",
                                    summary = "Handle exception when access forbidden",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 403,
                                              "path": "/api/v1/...",
                                              "error": "Forbidden",
                                              "message": "Access Denied"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleAccessDeniedException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(FORBIDDEN.value());
        errorResponse.setError(FORBIDDEN.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());

        return errorResponse;
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle exception when input data is conflicted",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 409,
                                              "path": "/api/v1/...",
                                              "error": "Conflict",
                                              "message": "{data} exists, Please try again!"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleDuplicateKeyException(InvalidDataException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    /*
     * Handle upload file exception
     *
     * @param e
     * @param request
     * @return error
     * */
    @ExceptionHandler(UploadFileException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle exception when upload file failed",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/api/v1/...",
                                              "error": "Bad Request",
                                              "message": "Upload file failed!"
                                            }
                                            """
                            ))})
    })
    public ErrorResponse handleUploadFileException(InvalidDataException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(KeycloakException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleKeycloakException(KeycloakException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri:", ""));
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(TOO_MANY_REQUESTS)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too Many Requests", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "429 Response", summary = "Handle exception when rate limit exceeded", value = """
                                                        {
                                                          "timestamp": "2023-10-19T06:07:35.321+00:00",
                                                          "status": 429,
                                                          "path": "/api/v1/auth/login",
                                                          "error": "Too Many Requests",
                                                          "message": "Rate limit exceeded. Please retry after 60 seconds.",
                                                          "retry_after_seconds": 60
                                                        }
                                                        """)) })
    })
    public ErrorResponse handleRateLimitExceededException(RateLimitExceededException e, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(TOO_MANY_REQUESTS.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri=", ""));
        errorResponse.setError(TOO_MANY_REQUESTS.getReasonPhrase());
        errorResponse.setMessage(String.format("Rate limit exceeded. Please retry after %d seconds.",
                e.getRetryAfterSeconds()));
        return errorResponse;
    }

    /**
     * Handle file validation exceptions (MIME type, magic bytes, size, virus)
     */
    @ExceptionHandler(FileValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "File Validation Failed", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "400 Response", summary = "Handle file validation errors", value = """
                                                        {
                                                          "timestamp": "2024-12-27T06:07:35.321+00:00",
                                                          "status": 400,
                                                          "path": "/api/v1/user/upload-avatar",
                                                          "error": "File Validation Failed",
                                                          "message": "Loại file 'application/x-php' không được hỗ trợ"
                                                        }
                                                        """)) })
    })
    public ErrorResponse handleFileValidationException(FileValidationException e, WebRequest webRequest) {
        log.warn("File validation failed: {} - {}", e.getErrorCode(), e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri=", ""));
        errorResponse.setError("File Validation Failed");
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

}
