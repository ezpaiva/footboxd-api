package com.footboxd.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.footboxd.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro JWT para autenticação baseada em token.
 * Extrai o token do header Authorization e valida.
 * Não bloqueia a requisição se o token for inválido - deixa para o handler global.
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String ctx = request.getContextPath();

        if (ctx != null && !ctx.isBlank() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        return path.startsWith("/auth/") || path.startsWith("/h2-console") || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // OPTIONS é sempre permitido
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // Se não houver token, continua (o controller vai validar autenticação)
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.debug("Nenhum token JWT encontrado no header");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        if (!StringUtils.hasText(token)) {
            log.debug("Token JWT vazio");
            filterChain.doFilter(request, response);
            return;
        }

        // Valida o token
        if (tokenService.tokenValido(token)) {
            try {
                String login = tokenService.getLoginDoToken(token);
                
                var auth = new UsernamePasswordAuthenticationToken(
                        login,
                        null,
                        Collections.emptyList());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                log.debug("Token válido para usuário: {}", login);
            } catch (Exception ex) {
                log.warn("Erro ao processar token válido: {}", ex.getMessage());
                // Continua sem autenticação
            }
        } else {
            log.debug("Token inválido ou expirado");
        }

        filterChain.doFilter(request, response);
    }
}
