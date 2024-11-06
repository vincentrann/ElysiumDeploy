package org.elysium.backend.models;

public class UserFactory {

    public static User createUser(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                AdminUser admin = new AdminUser();
                admin.setRole("admin");
                admin.setAdminLevel("High"); // Example of setting a role-specific field
                return admin;

            case "member":
                MemberUser member = new MemberUser();
                member.setRole("member");
                member.setMembershipType("Gold"); // Example of setting a role-specific field
                return member;

            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
