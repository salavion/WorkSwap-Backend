package org.workswap.listing.datasource.repository.types;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.types.EventSettings;

@Repository
public interface EventSettingsRepository extends JpaRepository<EventSettings, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO event_participants(event_id, user_id) VALUES (:eventId, :userId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void addParticipantById(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event_participants WHERE event_id = :eventId AND user_id = :userId", nativeQuery = true)
    void removeParticipantById(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
