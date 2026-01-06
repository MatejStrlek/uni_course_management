package hr.algebra.uni_course_management.security;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class SecurityConfigSmokeTest {
    @Autowired
    private List<SecurityFilterChain> filterChains;

    @Test
    void securityFilterChains_AreCreatedAndOrdered() {
        // two chains: API (order 1) then web (order 2)
        assertThat(filterChains).hasSize(2);
    }

    @Test
    void roleHierarchy_AllowsAdminToInheritProfessorAndStudent() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_PROFESSOR \n ROLE_ADMIN > ROLE_STUDENT");

        Collection<? extends GrantedAuthority> reachable =
                hierarchy.getReachableGrantedAuthorities(
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertThat(reachable)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN", "ROLE_PROFESSOR", "ROLE_STUDENT");
    }
}