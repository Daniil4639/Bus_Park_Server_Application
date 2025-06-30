package app.security.jwt;

import app.security.user_details.BusUserService;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final BusUserService busUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = "";

        Cookie[] cookies = request.getCookies();
        boolean tokenFound = false;

        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    authHeader = cookie.getValue();
                    tokenFound = true;
                    break;
                }
            }
        }

        String headerWithToken = request.getHeader("Authorization");

        if (!tokenFound && headerWithToken != null && headerWithToken.startsWith("Bearer ")) {
            authHeader = headerWithToken.substring(7);
            tokenFound = true;
        }

        if (!tokenFound) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(authHeader);

            if (!StringUtils.isEmpty(username)) {
                UserDetails details = busUserService.loadUserByUsername(username);

                if (jwtService.isTokenValid(authHeader, details)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            details.getUsername(), details.getPassword(),
                            details.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (SignatureException ignored) {}

        filterChain.doFilter(request, response);
    }
}