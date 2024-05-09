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
package com.chris.dao;

import com.chris.util.AuthCommon;
import com.chris.entity.AuthUser;
import com.chris.entity.UserStatus;

import java.util.List;

public interface AuthAccessDao {
    List<AuthUser> findAllUsers();

    List<AuthUser> findAllUsers(int startPage, int pageCount);

    List<AuthUser> findUserByName(String username);

    AuthUser findUserById(Integer id);

    AuthUser findUserByEmail(String email);
    AuthUser findUserByEmail(String email, boolean lock);

    boolean sameUserExists(String email);

    Integer saveAuthUser(AuthUser user);

    void updateUserRole(AuthUser user, AuthCommon roleType);

    void updateAuthUser(AuthUser user);

    void updateUserStatus(UserStatus status);

    void flipLoginUserStatus();

    void updateUserStatusAtomic(String email, AuthCommon status, Long session);

    void deleteAuthUserById(Integer userId);

    void deleteAuthUserByEmail(String email);
}
