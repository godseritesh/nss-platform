package com.nssplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Serves the React SPA for all non-API routes so that client-side routing works.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SpaFallbackInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/api/**", "/assets/**", "/favicon.ico",
                "/*.js", "/*.css", "/*.png", "/*.svg", "/*.ico", "/actuator/**");
    }

    /**
     * Forwards all non-API, non-asset requests to index.html for React Router.
     */
    static class SpaFallbackInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String path = request.getRequestURI();
            if (!path.startsWith("/api/") && !path.startsWith("/assets/") && !path.contains(".")) {
                request.getRequestDispatcher("/index.html").forward(request, response);
                return false;
            }
            return true;
        }
    }
}
