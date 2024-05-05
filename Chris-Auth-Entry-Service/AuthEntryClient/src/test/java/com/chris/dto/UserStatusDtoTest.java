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
import com.chris.entity.UserStatus;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserStatusDtoTest {

    @Order(1)
    @Test
    public void testUserStatusDto1() {
        AuthUserDto userDto = AuthUserDto.builder()
                .username("chris")
                .password("1234")
                .email("chris@auth.ca")
                .enabled(true)
                .build();

        UserStatusDto dto = UserStatusDto.builder()
                .status(AuthCommon.LOG_IN.getVal())
                .logInTimestamp(new Date(new Date().getTime() - 300_000L))
                .logOutTimestamp(new Date())
                .session(12*60*60*1000L)
                .user(userDto)
                .build();

        System.out.println("dto: " + dto.toString());

        UserStatus userStatusEntity = dto.toEntity();
        System.out.println("entity: " + userStatusEntity.toString());
    }

}