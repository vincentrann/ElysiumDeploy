package org.elysium.backend.models;

import jakarta.persistence.Entity;

@Entity
public class MemberUser extends User {

    private String membershipType; // Example of a role-specific field

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
}
