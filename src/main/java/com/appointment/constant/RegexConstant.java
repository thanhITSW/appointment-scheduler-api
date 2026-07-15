package com.appointment.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexConstant {
    public static final String PASSWORD_REGEX = "^(?!.*\\s)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&#^,.<>?/~`;:\"'+=_()\\-\\[\\]\\/\\\\|\\^\\$\\*\\+(){}]).{8,20}$";
}
