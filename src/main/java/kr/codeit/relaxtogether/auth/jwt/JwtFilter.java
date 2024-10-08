package kr.codeit.relaxtogether.auth.jwt;

import static kr.codeit.relaxtogether.exception.ErrorCode.AUTHENTICATION_FAIL;
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
        if (isPublicPattern(request.getRequestURI(), request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ApiException(AUTHENTICATION_FAIL);
        }
        String accessToken = authorization.split(" ")[1];
        if (!jwtTokenRepository.existsByToken(accessToken)) {
            throw new ApiException(AUTHENTICATION_FAIL);
        }
        if (jwtUtil.ieExpired(accessToken)) {
            throw new ApiException(TOKEN_EXPIRED);
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
        if (uri.equals("/api/reviews") && method.equals("GET")) {
            return true;
        }
        if (uri.equals("/api/gatherings") && method.equals("GET")) {
            return true;
        }

        PathPatternParser parser = new PathPatternParser();
        List<PathPattern> publicPatterns = List.of(
            parser.parse("/api/auths/check-email"),
            parser.parse("/api/auths/signup"),
            parser.parse("/api/auths/login"),
            parser.parse("/api/auths/logout"),
            parser.parse("/api/gatherings/{gatheringId:\\d+}"),
            parser.parse("/api/gatherings/{gatheringId:\\d+}/participants"),
            parser.parse("/api/auths/refresh-token"),
            parser.parse("/api/reviews/scores"),
            parser.parse("/h2-console/**"),
            parser.parse("/swagger-ui/**"),
            parser.parse("/v3/api-docs/**"),
            parser.parse("/swagger.json/**")
        );
        return publicPatterns.stream().anyMatch(pattern -> pattern.matches(PathContainer.parsePath(uri)));
    }
}
