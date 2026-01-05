package hr.algebra.uni_course_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoHandlerFoundException ex, HttpServletRequest request, Model model) {
        log.warn("Page not found: {}", request.getRequestURL(), ex);
        model.addAttribute("errorMessage", "The requested page was not found");
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request, Model model) {
        log.warn("Resource not found: {}", request.getRequestURL(), ex);
        model.addAttribute("errorMessage", "The requested resource was not found");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Internal server error at URL: {}", request.getRequestURL(), ex);
        model.addAttribute("errorMessage", "An unexpected error occurred");
        return "error/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        log.warn("Bad request at URL: {} - {}", request.getRequestURL(), ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/400";
    }
}