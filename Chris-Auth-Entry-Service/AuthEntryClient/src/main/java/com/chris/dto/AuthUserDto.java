package com.chris.dto;

import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * auth user dto transferred between client and server
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthUserDto extends BaseDtoToEntity<AuthUser> implements Serializable {
    private static final long serialVersionUID = -2338626292552177485L;
    private int id;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String email;

    @NonNull
    private Boolean enabled;

    //nullable
    private Set<RoleDto> roles;

    public AuthUserDto(@NonNull String username,
                       @NonNull String password,
                       @NonNull String email,
                       @NonNull Boolean enabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.roles = new HashSet<>();
    }

    public AuthUserDto(@NonNull String username,
                       @NonNull String password,
                       @NonNull String email,
                       @NonNull Boolean enabled,
                       @NonNull Set<RoleDto> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AuthUserDto) {
            AuthUserDto tmpDto = (AuthUserDto) o;

            if (!tmpDto.getUsername().equals(this.username) ||
                    !tmpDto.getPassword().equals(this.password) ||
                    !tmpDto.getEmail().equals(this.email) ||
                    tmpDto.getEnabled() != this.enabled) {
                return false;
            }

            return tmpDto.getId() == this.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        String base = String.format("%s:%s:%s:%s:%s", id, username, password, email, enabled);
        return base.hashCode();
    }


    @Override
    public AuthUser toEntity() {
        List<Role> roleEntities = new ArrayList<>();

        if (this.roles != null) {
            roleEntities = this.roles.stream()
                    .map(x -> x.toEntity())
                    .collect(Collectors.toList());
        }

        return new AuthUser(this.id, this.username,
                this.password, this.email,
                this.enabled, roleEntities);
    }

    @Override
    public boolean isValid() {
        return this.username != null ||
                this.password != null ||
                this.email != null ||
                this.enabled != null ||
                (this.roles == null ? false : !this.roles.isEmpty());
    }
}
