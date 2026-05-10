package org.artmotika.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.artmotika.common.dto.KycStatus;

@Entity @Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id private String id;
    private String walletAddress;
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus; 
    private Integer amlRiskScore;
    private String password; // hashed
    private String role; // e.g., "ROLE_USER", "ROLE_ADMIN"
    @Column(name = "is_qualified")
    private boolean qualified; // true = Qualified Investor
    @Column(name = "is_frozen")
    private boolean frozen;
}
