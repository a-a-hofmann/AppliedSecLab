package ch.ethz.asl.auth.config;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class CurrentRequest {

    private String flag;

    private void setRequest(HttpServletRequest request) {
        this.flag = request.getParameter(ConfigProperties.get().getFlag());
    }

    public boolean isFlagSet() {
        return !StringUtils.isEmpty(flag) && flag.equalsIgnoreCase(ConfigProperties.get().getFlagValue());
    }

    static CurrentRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            CurrentRequest currentRequest = new CurrentRequest();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            currentRequest.setRequest(request);
            return currentRequest;
        }
        throw new IllegalStateException("Not called in the context of an HTTP request");
    }
}
