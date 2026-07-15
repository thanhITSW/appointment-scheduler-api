package com.appointment.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodeConstant {
    public static final String UNAUTHORIZED = "error.authentication.unauthorize";
    public static final String FORBIDDEN = "error.access.forbidden";
    public static final String INTERNAL_SERVER = "error.server.internal";
    public static final String ERROR_NOT_FOUND = "error.not-found";
    public static final String ERROR_CONFLICT = "error.conflict";
    public static final String NOT_FOUND = "error.{0}.not-found";
    public static final String REQUIRED_VALIDATE = "error.required";
    public static final String INVALID_VALIDATE = "error.invalid";
    public static final String FIELD_INVALID_VALIDATE = "error.{0}.invalid";
    public static final String INVALID_LENGTH = "error.length";
    public static final String INVALID_EXISTED = "error.{0}.existed";

    // Authentication
    public static final String ERR_AUTHENTICATION_FAILED = "error.user.bad-credentials";
    public static final String ERR_USER_BLOCKED = "error.auth.user_blocked";
    public static final String ERR_ACCOUNT_LOCKED = "error.auth.account.locked";
    public static final String ERR_PASSWORD_EXPIRED = "error.auth.password.expired";

    public static final String ERR_USER_LOCKED = "error.user.locked";
    public static final String ERR_USER_INVALID = "error.user.invalid";
    public static final String ERR_ACCOUNT_ACTION_NOT_ALLOWED = "error.account.action.not.allowed";
    public static final String ERR_USER_ID_REQUIRED = "error.user.id.required";
    public static final String ERR_USERNAME_REQUIRED = "error.username.required";
    public static final String ERR_USER_NOT_FOUND = "error.user.not_found";

    // User
    public static final String ERR_FULL_NAME_REQUIRED = "error.full-name.required";
    public static final String ERR_NAME_REQUIRED = "error.username.required";
    public static final String ERR_NAME_SIZE = "error.user-name.size";
    public static final String ERR_FULL_NAME_SIZE = "error.full-name.size";
    public static final String ERR_EMAIL_DUPLICATED = "error.email.duplicated";
    public static final String ERR_EMPLOYEE_ID_DUPLICATED = "error.employee-id.duplicated";
    public static final String ERR_EMPLOYEE_ID_REQUIRED = "error.employee-id.required";

    // PASSWORD
    public static final String ERR_PASSWORD_REQUIRED = "error.user.password.required";
    public static final String ERR_PASSWORD_LENGTH = "error.user.password.length";
    public static final String ERR_PASSWORD_NOT_MATCH = "error.user.password.not.match";
    public static final String ERR_PASSWORD_INCORRECT = "error.user.password.incorrect";
    public static final String ERR_OLD_PASSWORD_INCORRECT = "error.user.old.password.incorrect";

    public static final String ERR_PASSWORD_CONFIRM_REQUIRED = "error.user.password.confirm.required";
    public static final String ERR_PASSWORD_CONFIRM_INCORRECT = "error.user.password.confirm.incorrect";
    public static final String ERR_PASSWORD_SAME_CURRENT_PASSWORD = "error.user.password.same.current.password";
    public static final String ERR_PASSWORD_REUSED = "error.user.password.reused";
    public static final String ERR_PASSWORD_MISSING_UPPERCASE = "error.user.password.missing.uppercase";
    public static final String ERR_PASSWORD_MISSING_LOWERCASE = "error.user.password.missing.lowercase";
    public static final String ERR_PASSWORD_MISSING_NUMBER = "error.user.password.missing.number";
    public static final String ERR_PASSWORD_MISSING_SPECIAL = "error.user.password.missing.special";
    public static final String ERR_PASSWORD_COMPLEXITY = "error.user.password.complexity";

    // Refresh token
    public static final String REFRESH_TOKEN_EXPIRED = "error.auth.refresh_token_expired";
    public static final String REFRESH_TOKEN_INVALID = "error.auth.refresh_token_invalid";
    public static final String INVALID_TOKEN_TYPE = "error.auth.invalid_token_type";

    public static final String ERR_UNKNOWN_FIELD = "validation.unknown_field";
    public static final String ERR_INVALID_JSON = "validation.invalid_json";

    // User Management
    public static final String ERR_USER_NAME_REQUIRED = "validation.name.required_field";
    public static final String ERR_USER_EMAIL_REQUIRED = "validation.email.required_field";
    public static final String ERR_USER_ROLE_REQUIRED = "validation.required_field";
    public static final String ERR_ADMIN_PASSWORD_REQUIRED = "validation.required_field";
    public static final String ERR_ADMIN_PASSWORD_INCORRECT = "error.admin.password.incorrect";
    public static final String ERR_USER_EMAIL_INVALID = "error.user.email.invalid";

    // Appointment domain
    public static final String ERR_CUSTOMER_NOT_FOUND = "error.customer.not_found";
    public static final String ERR_VEHICLE_NOT_FOUND = "error.vehicle.not_found";
    public static final String ERR_VEHICLE_NOT_OWNED_BY_CUSTOMER = "error.vehicle.not_owned_by_customer";
    public static final String ERR_SERVICE_TYPE_NOT_FOUND = "error.service_type.not_found";
    public static final String ERR_DEALERSHIP_NOT_FOUND = "error.dealership.not_found";
    public static final String ERR_SKILL_NOT_FOUND = "error.skill.not_found";
    public static final String ERR_TECHNICIAN_NOT_FOUND = "error.technician.not_found";
    public static final String ERR_SERVICE_BAY_NOT_FOUND = "error.service_bay.not_found";
    public static final String ERR_APPOINTMENT_NOT_FOUND = "error.appointment.not_found";
    public static final String ERR_APPOINTMENT_IN_PAST = "error.appointment.in_past";
    public static final String ERR_APPOINTMENT_CONFLICT = "error.appointment.conflict";
    public static final String ERR_APPOINTMENT_INVALID_STATUS = "error.appointment.invalid_status";
    public static final String ERR_NO_AVAILABLE_TECHNICIAN = "error.appointment.no_available_technician";
    public static final String ERR_NO_AVAILABLE_SERVICE_BAY = "error.appointment.no_available_service_bay";
    public static final String ERR_SKILL_CODE_DUPLICATED = "error.skill.code.duplicated";
    public static final String ERR_TECHNICIAN_EMPLOYEE_CODE_DUPLICATED = "error.technician.employee_code.duplicated";

    public static String getErrorCode(String code, Object... args) {
        return MessageFormat.format(code, args);
    }
}
