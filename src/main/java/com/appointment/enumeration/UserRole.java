package com.appointment.enumeration;

/**
 * Staff roles for the appointment domain.
 * Maps to Spring Security authorities as ROLE_{name}.
 */
public enum UserRole {
    ADVISOR,
    TECHNICIAN,
    MANAGER,
    ADMIN;

    public String toStringWithPrefix() {
        return "ROLE_%s".formatted(this.name());
    }

    public static boolean isAdvisor(UserRole role) {
        return role == ADVISOR;
    }

    public static boolean isTechnician(UserRole role) {
        return role == TECHNICIAN;
    }

    public static boolean isManager(UserRole role) {
        return role == MANAGER;
    }

    public static boolean isAdmin(UserRole role) {
        return role == ADMIN;
    }

    /** Roles that can manage bookings and customers. */
    public static boolean canManageAppointments(UserRole role) {
        return role == ADVISOR || role == MANAGER || role == ADMIN;
    }

    /** Roles that can manage master data (service types, bays, technicians, skills). */
    public static boolean canManageMasterData(UserRole role) {
        return role == MANAGER || role == ADMIN;
    }

    /** Roles that can manage users. */
    public static boolean canManageUsers(UserRole role) {
        return role == ADMIN;
    }
}
