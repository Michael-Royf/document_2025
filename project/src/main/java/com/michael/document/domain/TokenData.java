package com.michael.document.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Claims;
import java.util.List;

@Builder
@Getter
@Setter
public class TokenData {
    private User user;
    private Claims claims;
    private Boolean valid;
    private List<GrantedAuthority> authority;
}
