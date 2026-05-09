package com.nssplatform.events.repository;

import com.nssplatform.events.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByOrderByEventDateDesc(Pageable pageable);

    Page<Event> findByCategoryOrderByEventDateDesc(Event.Category category, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.category = 'BLOOD_DONATION'")
    long countBloodDonationEvents();
}
