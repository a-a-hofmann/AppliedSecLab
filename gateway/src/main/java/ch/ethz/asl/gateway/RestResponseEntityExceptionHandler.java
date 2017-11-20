package ch.ethz.asl.gateway;

import feign.FeignException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = FeignException.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        FeignException actualException = (FeignException) ex;
        if (actualException.status() == 418) {
            return userAttemptedToGetCertificateThatIsNotHis();
        } else if (actualException.status() == 410) {
            return userAttemptedToGetCertificateThatWasRevoked();
        }

        String bodyOfResponse = "Getting warmer..";

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            HttpSession session = servletRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    private ResponseEntity<Object> userAttemptedToGetCertificateThatIsNotHis() {
        String response = "Nice try...";

        ByteArrayResource resource = new ByteArrayResource(response.getBytes());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + "certificate" + ".txt")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(resource);
    }

    private ResponseEntity<Object> userAttemptedToGetCertificateThatWasRevoked() {
        String response = "Certificate was revoked!!!";

        ByteArrayResource resource = new ByteArrayResource(response.getBytes());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + "certificate" + ".txt")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(resource);
    }
}
