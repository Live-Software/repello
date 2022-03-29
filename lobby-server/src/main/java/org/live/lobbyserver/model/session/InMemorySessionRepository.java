package org.live.lobbyserver.model.session;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemorySessionRepository implements SessionRepository{

    private final Map<String, Session> sessions;

    public InMemorySessionRepository() {
        this.sessions = new HashMap<>();
    }

    @Override
    public Optional<Session> findById(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<Session> findAllByLastInteractionLessThan(Instant timeout) {
        return sessions.values().stream()
                .filter(session -> session.getLastInteraction().isBefore(timeout))
                .toList();
    }

    @Override
    public void save(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void deleteAll(List<Session> sessionsToRemove) {
        sessionsToRemove.stream()
                .map(Session::getId)
                .forEach(sessions::remove);
    }
}
