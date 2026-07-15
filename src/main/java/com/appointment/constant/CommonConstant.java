package com.appointment.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {
    public static final String TOKEN_COOKIE_NAME = "auth-session";
}
