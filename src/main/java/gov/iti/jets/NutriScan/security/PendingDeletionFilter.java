package gov.iti.jets.NutriScan.security;

import gov.iti.jets.NutriScan.exception.AccountPendingDeletionException;
import gov.iti.jets.NutriScan.model.AccountStatus;
import gov.iti.jets.NutriScan.model.User;
import gov.iti.jets.NutriScan.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingDeletionFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    // injected to handle exceptions in the servlet filter layer
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {

            UUID keycloakId = UUID.fromString(jwtAuth.getToken().getSubject());

            User user = userRepository.findById(keycloakId).orElseThrow();

            if (user.getAccountStatus() == AccountStatus.PENDING_DELETION
                && !isAllowedForPendingDeletion(request)) {

                handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new AccountPendingDeletionException(
                        "Account is already scheduled for deletion on " + user.getToBeDeletedAt()));

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedForPendingDeletion(HttpServletRequest request) {

        log.info("{} {}", request.getMethod(), request.getRequestURI());

        return request.getMethod().equals("POST")
            && request.getRequestURI().equals("/api/v1/users/profile/restore");
    }
}