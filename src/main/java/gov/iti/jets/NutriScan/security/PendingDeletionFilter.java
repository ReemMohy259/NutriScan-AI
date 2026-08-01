package gov.iti.jets.NutriScan.security;

import gov.iti.jets.NutriScan.model.AccountStatus;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PendingDeletionFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {

            UUID keycloakId = UUID.fromString(jwtAuth.getToken().getSubject());

            User user = userRepository.findById(keycloakId).orElseThrow();

            if (user.getAccountStatus() == AccountStatus.PENDING_DELETION) {

                response.setStatus(HttpStatus.CONFLICT.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                response.getWriter().write("""
                    {
                      "code":"ACCOUNT_PENDING_DELETION"
                    }
                    """);

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}