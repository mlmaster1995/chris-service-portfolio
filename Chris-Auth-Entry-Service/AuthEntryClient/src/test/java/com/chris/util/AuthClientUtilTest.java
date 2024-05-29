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
package com.chris.util;

import com.chris.exception.AuthClientException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthClientUtilTest {
    @Order(1)
    @Test
    public void testEmail() {
        String email = "chris@test.com";
        assertDoesNotThrow(() -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(2)
    @Test
    public void testEmail2() {
        String email = "christest.com";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(3)
    @Test
    public void testEmail3() {
        String email = "chris@test";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(4)
    @Test
    public void testEmail4() {
        String email = "chris@.ca";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(5)
    @Test
    public void testEmail5() {
        String email = "chris@.";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }

    @Order(6)
    @Test
    public void testEmail6() {
        String email = "@.";
        assertThrows(AuthClientException.class,
                () -> AuthClientUtil.validateEmailAddress(email));
    }
}