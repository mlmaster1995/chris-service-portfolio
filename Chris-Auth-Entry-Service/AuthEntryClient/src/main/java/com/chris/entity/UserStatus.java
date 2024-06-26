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
package com.chris.entity;

import com.chris.dto.UserStatusDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * user_status entity used for spring-data-jpa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_status")
public class UserStatus {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private String status;

    @Column(name = "login_timestamp")
    private Date logInTimestamp;

    @Column(name = "logout_timestamp")
    private Date logOutTimestamp;

    @Column(name = "session")
    private Long session;

    @OneToOne(fetch = FetchType.EAGER, cascade =
            {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private AuthUser authUser;

    public UserStatus(String status) {
        this.status = status;
    }

    public UserStatus(String status,
                      AuthUser authUser) {
        this.status = status;
        this.authUser = authUser;
    }

    //used from dto to entity
    public UserStatus(String status,
                      Date logInTimestamp,
                      Date logOutTimestamp,
                      Long session) {
        this.status = status;
        this.logInTimestamp = logInTimestamp;
        this.logOutTimestamp = logOutTimestamp;
        this.session = session;
    }

    public UserStatus(String status,
                      Date logInTimestamp,
                      Date logOutTimestamp,
                      Long session,
                      AuthUser user) {
        this.status = status;
        this.logInTimestamp = logInTimestamp;
        this.logOutTimestamp = logOutTimestamp;
        this.session = session;
        this.authUser = user;
    }

    /**
     * entity to dto
     *
     * @return
     */
    public UserStatusDto toDto() {
        return UserStatusDto.builder()
                .status(this.status)
                .session(this.session)
                .id(this.id)
                .logInTimestamp(this.logInTimestamp)
                .logOutTimestamp(this.logOutTimestamp)
                .build();
    }


    @Override
    public String toString() {
        return String.format("{id:%s, status:%s, login: %s, logout: %s, session: %s}",
                id, status,
                logInTimestamp == null ? null : logInTimestamp.toString(),
                logOutTimestamp == null ? null : logOutTimestamp.toString(),
                session);
    }
}
