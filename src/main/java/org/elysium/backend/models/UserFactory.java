package org.elysium.backend.models;

public class UserFactory {

    public static User createUser(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                AdminUser admin = new AdminUser();
                admin.setAdminLevel("Admin"); // Example of role-specific field
                return admin;

            case "member":
                MemberUser member = new MemberUser();
                member.setMembershipType("Member"); // Example of role-specific field
                return member;

            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
