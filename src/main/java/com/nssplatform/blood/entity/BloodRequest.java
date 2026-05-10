package com.nssplatform.blood.entity;

import com.nssplatform.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blood_requests", indexes = {
    @Index(name = "idx_br_status",     columnList = "status"),

    @Index(name = "idx_br_blood_group",columnList = "blood_group"),
    @Index(name = "idx_br_district",   columnList = "district")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 10)
    private BloodGroup bloodGroup;

    @Column(name = "units_needed", nullable = false)
    private Integer unitsNeeded;

    @Column(nullable = false, length = 200)
    private String hospital;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String state = "Maharashtra";

    /** Coordinates for OpenStreetMap pin */
    @Column(columnDefinition = "numeric")
    private Double latitude;
    @Column(columnDefinition = "numeric")
    private Double longitude;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 15)
    private String contactPhone;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Urgency urgency = Urgency.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bloodRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DonorInterest> donorInterests = new ArrayList<>();

    public boolean isUrgent() {
        return urgency == Urgency.CRITICAL || urgency == Urgency.URGENT;
    }



    // ── Enums ──────────────────────────────────────────
    public enum BloodGroup { A_POS, A_NEG, B_POS, B_NEG, O_POS, O_NEG, AB_POS, AB_NEG }
    public enum Urgency    { CRITICAL, URGENT, STANDARD }
    public enum Status     { OPEN, FULFILLED, EXPIRED }
}
