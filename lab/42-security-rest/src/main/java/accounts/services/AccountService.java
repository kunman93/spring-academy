package accounts.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    /* Here we are implementing method security.
     * For this to work, the @Configuration class `RestSecurityConfig` has to be annotated with `@EnableMethodSecurity`
     *
     * - Re-run this application
     * - Using Chrome Incognito browser, access
     *   http://localhost:8080/authorities?username=user
     * - Enter "user"/"user" and verify that 403 failure occurs
     * - If you want to use "curl", use
     *   curl -i -u user:user http://localhost:8080/authorities?username=user
     *
     * - Close the Chrome Incognito browser and start a new one
     * - Access http://localhost:8080/authorities?username=admin
     * - Enter "admin"/"admin" and verify that the roles are displayed successfully
     * - If you want to use "curl", use
     *   curl -i -u admin:admin http://localhost:8080/authorities?username=admin
     *
     * - Close the Chrome Incognito browser and start a new one
     * - Access http://localhost:8080/authorities?username=superadmin
     * - Enter "superadmin"/"superadmin" and verify that the roles are displayed successfully
     * - If you want to use "curl", use
     *   curl -i - u superadmin: superadmin http://localhost:8080/authorities?username=superadmin
     */
    @PreAuthorize("hasRole('ADMIN') && #username == principal.username")
    public List<String> getAuthoritiesForUser(String username) {
        Collection<? extends GrantedAuthority> grantedAuthorities = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities();

        return grantedAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }

}
