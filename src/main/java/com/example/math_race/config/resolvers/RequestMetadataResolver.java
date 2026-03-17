package com.example.math_race.config.resolvers;

import com.example.math_race.dto.request.RequestMetadata;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class RequestMetadataResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RequestMetadata.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        RequestMetadata metadata = new RequestMetadata();

        metadata.setUserAgent(webRequest.getHeader("User-Agent"));

        metadata.setGuestId(webRequest.getHeader("GuestID"));

        String authHeader = webRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            metadata.setAuthorization(authHeader.substring(7));
        } else {
            metadata.setAuthorization(authHeader);
        }

        String ipFromHeader = webRequest.getHeader("X-Forwarded-For");
        String finalIp;

        if (ipFromHeader == null || ipFromHeader.isEmpty() || "unknown".equalsIgnoreCase(ipFromHeader)) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            finalIp = (request != null) ? request.getRemoteAddr() : "unknown";
        } else {
            finalIp = ipFromHeader.split(",")[0].trim();
        }

        metadata.setIpAddress(finalIp);

        return metadata;
    }
}