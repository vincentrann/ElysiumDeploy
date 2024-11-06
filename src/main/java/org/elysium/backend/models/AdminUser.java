package org.elysium.backend.models;

import jakarta.persistence.Entity;

@Entity
public class AdminUser extends User {

    private String adminLevel; // Example of a role-specific field

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }
}
