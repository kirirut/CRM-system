package com.example.srmsystem.config;

import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.security.JwtUtil;
import com.example.srmsystem.service.CustomerService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;


@WebFilter("/*")// Обрабатывает все запросы
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра (если нужно)
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Пропускаем запросы к пути аутентификации
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path.startsWith("/api/customers") && "POST".equals(method)) {
            filterChain.doFilter(request, response);
            return;  // Прерываем выполнение фильтра для путей аутентификации
        }


        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // Пропускаем запрос, если нет токена
            return;
        }

        jwt = authHeader.substring(7);  // Убираем "Bearer "
        username = jwtUtil.extractUsername(jwt);

        // Если пользователь не аутентифицирован
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            DisplayCustomerDto customer = customerService.getCustomerByUsername(username);

            // Если токен валиден
            if (jwtUtil.isTokenValid(jwt, customer)) {
                // Без ролей (передаем null или пустой список для authorities)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        customer, null, null  // Нет ролей
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);  // Пропускаем запрос дальше
    }

    @Override
    public void destroy() {
        // Очистка ресурсов, если необходимо
    }
}
