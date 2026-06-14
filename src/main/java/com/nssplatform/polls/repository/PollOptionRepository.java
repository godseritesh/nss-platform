package com.nssplatform.polls.repository;

import com.nssplatform.polls.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
    List<PollOption> findByPollIdOrderByDisplayOrderAsc(Long pollId);

    @Transactional
    void deleteByPollId(Long pollId);
}
