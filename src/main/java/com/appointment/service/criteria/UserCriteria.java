package com.appointment.service.criteria;

import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.jpa.specifications.Criteria;
import com.appointment.jpa.specifications.filter.Filter;
import com.appointment.jpa.specifications.filter.InstantFilter;
import com.appointment.jpa.specifications.filter.LongFilter;
import com.appointment.jpa.specifications.filter.StringFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria implements Serializable, Criteria {
    private LongFilter id;
    private StringFilter email;
    private StringFilter fullName;
    private InstantFilter fromDate;
    private InstantFilter toDate;
    private UserRoleFilter role;
    private UserStatusFilter status;


    public UserCriteria(UserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.fromDate = other.fromDate == null ? null : other.fromDate.copy();
        this.toDate = other.toDate == null ? null : other.toDate.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.status = other.status == null ? null : other.status.copy();
    }

    @Override
    public Criteria copy() {
        return new UserCriteria(this);
    }

    @NoArgsConstructor
    public static class UserRoleFilter
            extends Filter<UserRole> {
        public UserRoleFilter(UserRoleFilter other) {
            super(other);
        }

        @Override
        public UserRoleFilter copy() {
            return new UserRoleFilter(this);
        }

    }

    @NoArgsConstructor
    public static class UserStatusFilter
            extends Filter<UserStatus> {
        public UserStatusFilter(UserStatusFilter other) {
            super(other);
        }

        @Override
        public UserStatusFilter copy() {
            return new UserStatusFilter(this);
        }

    }
}
