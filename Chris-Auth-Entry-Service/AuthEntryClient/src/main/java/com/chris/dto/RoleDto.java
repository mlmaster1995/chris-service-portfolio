package com.chris.dto;

import com.chris.entity.AuthUser;
import com.chris.entity.Role;
import com.chris.entity.RoleType;
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

    //nullable
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


    @Override
    public Role toEntity() {
        List<AuthUser> authUserEntities = new ArrayList<>();

        if (users != null) {
            authUserEntities = users.stream().map(
                            dto -> new AuthUser(dto.getId(), dto.getUsername(),
                                    dto.getPassword(), dto.getEmail(),
                                    dto.getEnabled(), null))
                    .collect(Collectors.toList());
        }

        return new Role(roleType, authUserEntities);
    }

    @Override
    public boolean isValid() {
        for (RoleType type : RoleType.values()) {
            if (!type.getVal().equals(roleType)) {
                return false;
            }
        }

        return true;
    }
}
