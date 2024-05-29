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
import com.chris.entity.AuthUser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthUserDtoTest {

    @Order(1)
    @Test
    public void testAuthUserDtoConstructorWithDefaultRoleType() {
        AuthUserDto userDto = new AuthUserDto("chris", "1234",
                "chris@chrisauth.ca", true);

        System.out.println("auth user: " + userDto.toString());

        assertNull(userDto.getStatus());
    }

    @Order(2)
    @Test
    public void testAuthUserDtoTestDefaultBuilder() {
        AuthUserDto userDto = AuthUserDto.builder()
                .username("chris")
                .password("1234")
                .email("chris@chrisauth.ca")
                .enabled(true)
                .build();

        System.out.println("auth user: " + userDto.toString());

        assertNull(userDto.getStatus());
        assertNotNull(userDto.getRoles());
    }

    @Order(3)
    @Test
    public void testAuthUserDtoTestWithUserStatusDto() {


        UserStatusDto userStatusDto = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .logInTimestamp(new Date())
                .logOutTimestamp(new Date(new Date().getTime() - 3000L))
                .user(new AuthUserDto("chris", "1234",
                        "chris@chrisauth.ca"))
                .session(100L)
                .build();

        AuthUserDto userDto = AuthUserDto.builder()
                .username("chris")
                .password("1234")
                .email("chris@chrisauth.ca")
                .enabled(true)
                .status(userStatusDto)
                .build();

        System.out.println("auth user: " + userDto.toString());
    }

    @Order(4)
    @Test
    public void testAuthUserDtoTestWithRoleDto() {
        RoleDto roleDto = RoleDto.builder()
                .roleType(AuthCommon.LOG_IN.getVal())
                .users(new HashSet<>(Arrays.asList(
                        new AuthUserDto("chris", "1234",
                                "chris@chrisauth.ca", true))))
                .build();

        AuthUserDto userDto = AuthUserDto.builder()
                .username("chris")
                .password("1234")
                .email("chris@chrisauth.ca")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(roleDto)))
                .build();

        System.out.println("auth user: " + userDto.toString());
    }

    @Order(5)
    @Test
    public void testAuthUserDtoTestWithAll() {
        UserStatusDto userStatusDto = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .logInTimestamp(new Date())
                .logOutTimestamp(new Date(new Date().getTime() - 3000L))
                .user(new AuthUserDto("chris", "1234",
                        "chris@chrisauth.ca", true))
                .session(100L)
                .build();

        RoleDto roleDto = RoleDto.builder()
                .roleType(AuthCommon.USER.getVal())
                .users(new HashSet<>(Arrays.asList(
                        new AuthUserDto("chris", "1234",
                                "chris@chrisauth.ca", true))))
                .build();

        AuthUserDto userDto = AuthUserDto.builder()
                .username("chris")
                .password("1234")
                .email("chris@chrisauth.ca")
                .enabled(true)
                .status(userStatusDto)
                .roles(new HashSet<>(Arrays.asList(roleDto)))
                .build();

        System.out.println("auth user: " + userDto.toString());

        AuthUser userEntity = userDto.toEntity();
        System.out.println("auth user entity: " + userEntity.toString());
    }

}