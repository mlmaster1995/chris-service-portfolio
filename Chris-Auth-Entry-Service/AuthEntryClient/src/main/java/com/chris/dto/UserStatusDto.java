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
import com.chris.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Date;

/**
 * user status dto transferred between client and server
 */
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserStatusDto extends BaseDtoToEntity<UserStatus> implements Serializable {
    private static final long serialVersionUID = -2338626292552197485L;

    private int id;

    @NonNull
    private String status;

    @NonNull
    @JsonProperty("login_timestamp")
    private Date logInTimestamp;

    @NonNull
    @JsonProperty("logout_timestamp")
    private Date logOutTimestamp;

    @NonNull
    private AuthUserDto authUserDto;

    public UserStatusDto(@NonNull String status,
                         @NonNull Date logInTimestamp,
                         @NonNull Date logOutTimestamp,
                         @NonNull AuthUserDto authUserDto) {
        this.status = status;
        this.logInTimestamp = logInTimestamp;
        this.logOutTimestamp = logOutTimestamp;
        this.authUserDto = authUserDto;
    }

    /**
     * do not use authUserDto.toEntity -> it will trigger infinite loop
     *
     * @return
     */
    @Override
    public UserStatus toEntity() {
        return new UserStatus(id, status, logInTimestamp, logOutTimestamp,
                new AuthUser(this.authUserDto.getId(), this.authUserDto.getUsername(),
                        this.authUserDto.getPassword(), this.authUserDto.getEmail(),
                        this.authUserDto.getEnabled(), null, null));
    }

    @Override
    public boolean isValid() {
        if ((!status.equals(AuthCommon.LOG_IN.getVal()) &&
                !status.equals(AuthCommon.LOG_OUT.getVal())) ||
                id < 0 ||
                logInTimestamp == null ||
                logOutTimestamp == null ||
                authUserDto == null) {
            return false;
        }

        return true;
    }
}
