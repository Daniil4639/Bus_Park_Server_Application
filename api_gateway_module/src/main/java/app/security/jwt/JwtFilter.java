package app.security.jwt;

import app.security.user_details.BusUserService;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;

    private final BusUserService busUserService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return findToken(exchange)
                .flatMap(token -> {
                    try {
                        String username = jwtService.extractUsername(token);

                        if (!StringUtils.isEmpty(username)) {
                            UserDetails details = busUserService.loadUserByUsername(username);

                            if (jwtService.isTokenValid(token, details)) {
                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                        details.getUsername(), details.getPassword(),
                                        details.getAuthorities()
                                );

                                return chain.filter(exchange).contextWrite(
                                        ReactiveSecurityContextHolder.withAuthentication(auth)
                                );
                            }
                        }
                    } catch (SignatureException ignored) {}

                    return Mono.empty();
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<String> findToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Mono.just(header.substring(7));
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwtToken");
        if (cookie != null) {
            return Mono.just(cookie.getValue());
        }

        return Mono.empty();
    }
}