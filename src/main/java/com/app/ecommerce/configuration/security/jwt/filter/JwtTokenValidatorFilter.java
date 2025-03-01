package com.app.ecommerce.configuration.security.jwt.filter;


import com.app.ecommerce.configuration.security.HashUtil;
import com.app.ecommerce.configuration.security.UserPrincipal;
import com.app.ecommerce.configuration.security.constant.SecurityConstants;
import com.app.ecommerce.configuration.security.jwt.JwtService;
import com.app.ecommerce.core.user.User;
import com.app.ecommerce.core.user.UserRepository;
import com.app.ecommerce.core.user.utils.UserRole;
import com.app.ecommerce.core.userToken.UserTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@AllArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {
    public JwtService jwtService;
    public UserRepository userRepository;
    public UserTokenRepository userTokenRepository;
    public HashUtil hashUtil = new HashUtil();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.ACCESS_KEY.getBytes(StandardCharsets.UTF_8));

        try {
            Claims claims = jwtService.getClaimsFromToken(token, key);
            String username = (String) claims.get("username");
            String authorities = (String) claims.get("authorities");

            if (username != null && authorities != null) {
                Optional<User> user = userRepository.findByEmail(username);

                if (user.isPresent()) {
                    if (userTokenRepository.findByAccessTokenAndUserRoleAndUser(hashUtil.hashToken(token), UserRole.valueOf(authorities),user.get()) != null ) {
                        UserPrincipal userPrincipal = new UserPrincipal(user.get());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userPrincipal, null,  AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        response.getWriter().write("Access token is not allowed");
                        return;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Access token is unauthorized");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Access token is unauthorized");
                return;
            }

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token has expired");
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }
        chain.doFilter(request, response);
    }
}
