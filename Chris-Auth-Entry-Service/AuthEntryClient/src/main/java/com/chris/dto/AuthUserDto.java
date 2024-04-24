/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Chris Yang
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.dto;

import com.chris.entity.AuthCommon;
import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
    private UserStatusDto userStatus;

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
        this.roles = new HashSet<>(Arrays.asList(new RoleDto(AuthCommon.USER.getVal())));
    }

    public AuthUserDto(@NonNull String username,
                       @NonNull String password,
                       @NonNull String email,
                       @NonNull Boolean enabled,
                       @NonNull UserStatusDto userStatus) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.userStatus = userStatus;
        this.roles = new HashSet<>(Arrays.asList(new RoleDto(AuthCommon.USER.getVal())));
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

    public AuthUserDto(@NonNull String username,
                       @NonNull String password,
                       @NonNull String email,
                       @NonNull Boolean enabled,
                       @NonNull UserStatusDto userStatus,
                       @NonNull Set<RoleDto> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.userStatus = userStatus;
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

        UserStatus userStatusEntity = null;
        if (this.userStatus != null) {
            userStatusEntity = userStatus.toEntity();
        }

        return new AuthUser(this.id, this.username,
                this.password, this.email,
                this.enabled, userStatusEntity, roleEntities);
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
