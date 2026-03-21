package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Lấy các quyền mặc định (scopes)
        Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);

        // Gộp với Roles từ Keycloak
        Set<GrantedAuthority> keycloakAuthorities = Stream.concat(
                authorities.stream(),
                extractKeycloakRoles(jwt).stream()).collect(Collectors.toSet());

        // Trả về Authentication Token với name là "preferred_username" thay vì UUID của
        // sub
        String principalClaimName = jwt.getClaimAsString("preferred_username");
        return new JwtAuthenticationToken(jwt, keycloakAuthorities, principalClaimName);
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractKeycloakRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Set.of();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles == null) {
            return Set.of();
        }

        // Map roles thành chuẩn ROLE_xxx của Spring Security
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
