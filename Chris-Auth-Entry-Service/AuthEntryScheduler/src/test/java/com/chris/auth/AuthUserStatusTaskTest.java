package com.chris.auth;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;

class AuthUserStatusTaskTest {

    @Order(1)
    @Test
    public void dateTest() {
        LocalDateTime now = LocalDateTime.now();
        var expression = CronExpression.parse("0 */1 * * * ?");
        var result = expression.next(now);
        System.out.println("now: " + now);
        System.out.println("next: " + result);
    }

}