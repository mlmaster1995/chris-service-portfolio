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

import com.chris.util.AuthCommon;
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
 * role dto used between client and the server
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleDto extends BaseDtoToEntity<Role> implements Serializable {
    private static final long serialVersionUID = -2338626292552177485L;

    private int id;

    @NonNull
    private String roleType;

    @NonNull
    private Set<AuthUserDto> users;

    public RoleDto(@NonNull String roleType) {
        this.roleType = roleType;
        this.users = new HashSet<>();
    }

    public RoleDto(@NonNull String name,
                   @NonNull Set<AuthUserDto> users) {
        this.roleType = name;
        this.users = users;
    }

    public void addAuthUserDto(AuthUserDto userDto) {
        if (users == null) {
            users = new HashSet<>();
        }

        users.add(userDto);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RoleDto) {
            RoleDto tmpDto = (RoleDto) o;

            if (tmpDto.roleType != this.roleType) {
                return false;
            } else {
                return tmpDto.id == this.id;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.roleType.hashCode();
    }


    /**
     * do not use authUserDto.toEntity -> it will trigger infinite loop
     *
     * @return
     */
    @Override
    public Role toEntity() {
        List<com.chris.entity.AuthUser> authUserEntities = new ArrayList<>();

        if (users != null) {
            authUserEntities = users.stream()
                    .map(dto -> new com.chris.entity.AuthUser(dto.getId(),
                            dto.getUsername(), dto.getPassword(),
                            dto.getEmail(), dto.getEnabled(),
                            null, null))
                    .collect(Collectors.toList());
        }

        return new Role(roleType, authUserEntities);
    }

    @Override
    public boolean isValid() {
        if (!this.roleType.equals(AuthCommon.USER.getVal()) &&
                !this.roleType.equals(AuthCommon.ADMIN.getVal())) {
            return false;
        }

        return true;
    }
}
