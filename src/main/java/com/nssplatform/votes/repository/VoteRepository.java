package com.nssplatform.votes.repository;

import com.nssplatform.votes.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByUserIdAndPollId(Long userId, Long pollId);

    @Query("SELECT v.pollOption.id, COUNT(v) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.pollOption.id")
    List<Object[]> countVotesByOption(Long pollId);

    @Query("SELECT COUNT(DISTINCT v.poll.id) FROM Vote v WHERE v.user.id = :userId")
    long countDistinctPollsVotedByUser(Long userId);

    long countByPollId(Long pollId);

    long count();

    @Query("SELECT COUNT(v) FROM Vote v JOIN v.poll p JOIN p.event e WHERE e.category = 'BLOOD_DONATION'")
    long countBloodDonationVotes();
}
