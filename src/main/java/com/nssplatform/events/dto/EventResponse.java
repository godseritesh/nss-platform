package com.nssplatform.events.dto;

import com.nssplatform.events.entity.Event;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String location;
    private String category;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse from(Event e) {
        return EventResponse.builder()
            .id(e.getId())
            .title(e.getTitle())
            .description(e.getDescription())
            .eventDate(e.getEventDate())
            .location(e.getLocation())
            .category(e.getCategory().name())
            .createdByName(e.getCreatedBy() != null ? e.getCreatedBy().getName() : "NSS Admin")
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }
}
