package kr.codeit.relaxtogether.auth.jwt;

import static kr.codeit.relaxtogether.exception.ErrorCode.TOKEN_EXPIRED;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authorization.split(" ")[1];
        if (!jwtTokenRepository.existsByToken(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (jwtUtil.ieExpired(accessToken)) {
            if (!isPublicPattern(request.getRequestURI(), request.getMethod())) {
                throw new ApiException(TOKEN_EXPIRED);
            }
            filterChain.doFilter(request, response);
            return;
        }

        User user = User.builder()
            .email(jwtUtil.getEmail(accessToken))
            .build();
        Authentication authToken = new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null,
            Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPattern(String uri, String method) {
        if (uri.equals("/api/reviews") && !method.equals("GET")) {
            return false;
        }

        PathPatternParser parser = new PathPatternParser();
        List<PathPattern> publicPatterns = List.of(
            parser.parse("/api/auths/check-email"),
            parser.parse("/api/auths/signup"),
            parser.parse("/api/auths/login"),
            parser.parse("/api/gatherings/{gatheringId}"),
            parser.parse("/api/gatherings/{gatheringId}/participants"),
            parser.parse("/api/reviews"),
            parser.parse("/h2-console/**"),
            parser.parse("/swagger-ui/**"),
            parser.parse("/v3/api-docs/**"),
            parser.parse("/swagger.json/**")
        );
        return publicPatterns.stream().anyMatch(pattern -> pattern.matches(PathContainer.parsePath(uri)));
    }
}
