package com.nssplatform.votes.entity;

import com.nssplatform.auth.entity.User;
import com.nssplatform.polls.entity.Poll;
import com.nssplatform.polls.entity.PollOption;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes",
    indexes = {
        @Index(name = "idx_votes_user_id", columnList = "user_id"),
        @Index(name = "idx_votes_poll_id", columnList = "poll_id"),
        @Index(name = "idx_votes_poll_option_id", columnList = "poll_option_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_votes_user_poll", columnNames = {"user_id", "poll_id"})
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
