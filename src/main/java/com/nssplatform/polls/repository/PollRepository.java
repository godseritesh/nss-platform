package com.nssplatform.polls.repository;

import com.nssplatform.polls.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.options WHERE p.event.id = :eventId ORDER BY p.createdAt DESC")
    List<Poll> findByEventIdWithOptions(Long eventId);

    long countByStatus(Poll.Status status);
}
