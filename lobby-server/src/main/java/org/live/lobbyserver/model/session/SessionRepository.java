package org.live.lobbyserver.model.session;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(String id);

    List<Session> findAllByLastInteractionLessThan(Instant timeout);

    void save(Session session);

    void deleteAll(List<Session> sessions);

}
