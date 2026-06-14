package com.nssplatform.blood.dto;

import com.nssplatform.blood.entity.BloodRequest;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class BloodRequestResponse {
    private Long id;
    private String patientName;
    private String bloodGroup;
    private Integer unitsNeeded;
    private String hospital;
    private String city;
    private String district;
    private String state;
    private Double latitude;
    private Double longitude;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String description;
    private String urgency;

    private String status;
    private long donorCount;
    private LocalDateTime createdAt;

    public static BloodRequestResponse from(BloodRequest r, long donorCount) {
        return BloodRequestResponse.builder()
            .id(r.getId())
            .patientName(r.getPatientName())
            .bloodGroup(formatBloodGroup(r.getBloodGroup()))
            .unitsNeeded(r.getUnitsNeeded())
            .hospital(r.getHospital())
            .city(r.getCity())
            .district(r.getDistrict())
            .state(r.getState())
            .latitude(r.getLatitude())
            .longitude(r.getLongitude())
            .contactName(r.getContactName())
            .contactPhone(r.getContactPhone())
            .contactEmail(r.getContactEmail())
            .description(r.getDescription())
            .urgency(r.getUrgency().name())

            .status(r.getStatus().name())
            .donorCount(donorCount)
            .createdAt(r.getCreatedAt())
            .build();
    }

    private static String formatBloodGroup(BloodRequest.BloodGroup bg) {
        return switch (bg) {
            case A_POS  -> "A+";   case A_NEG  -> "A-";
            case B_POS  -> "B+";   case B_NEG  -> "B-";
            case O_POS  -> "O+";   case O_NEG  -> "O-";
            case AB_POS -> "AB+";  case AB_NEG -> "AB-";
        };
    }
}
