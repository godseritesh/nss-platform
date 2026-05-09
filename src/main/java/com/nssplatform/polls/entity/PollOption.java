package com.nssplatform.polls.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "poll_options", indexes = {
    @Index(name = "idx_poll_options_poll_id", columnList = "poll_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(name = "option_text", nullable = false, length = 300)
    private String optionText;

    @Column(name = "display_order")
    private Integer displayOrder;
}
