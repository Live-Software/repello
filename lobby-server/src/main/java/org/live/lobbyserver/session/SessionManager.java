package org.live.lobbyserver.session;

import org.live.lobbyserver.model.player.Player;
import org.live.lobbyserver.model.player.PlayerRepository;
import org.live.lobbyserver.model.session.Session;
import org.live.lobbyserver.model.session.SessionRepository;
import org.live.lobbyserver.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private final DateUtil dateUtil;
    private final SessionRepository sessionRepository;
    private final PlayerRepository playerRepository;
    private final int sessionTimeoutSeconds;

    @Autowired
    public SessionManager(
            DateUtil dateUtil,
            SessionRepository sessionRepository,
            PlayerRepository playerRepository,
            @Value("${session.timeout.seconds}") int sessionTimeoutSeconds
    ) {
        this.dateUtil = dateUtil;
        this.sessionRepository = sessionRepository;
        this.playerRepository = playerRepository;
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
    }

    public void addPlayerToSession(String code, String id) {
        var player = playerRepository.findById(id).orElse(new Player(id));
        var session = sessionRepository.findById(code).orElse(new Session(code));
        player.setSession(session);
        session.getPlayers().add(player);
        playerRepository.save(player);
        sessionRepository.save(session);
    }

    public void disconnectPlayer(String id) {
        var player = playerRepository.findById(id);
        if (player.isEmpty()) {
            return;
        }
        var sessionId = player.get().getSession().getId();
        var maybeSession = sessionRepository.findById(sessionId);
        if (maybeSession.isEmpty()) {
            return;
        }
        var session = maybeSession.get();
        session.getPlayers().remove(player.get());
        sessionRepository.save(session);
    }

    @Scheduled(cron = "${session.cleanup.cron}")
    public void removeUnusedSessions() {
        var timeout = dateUtil.getCurrentTime().minusSeconds(sessionTimeoutSeconds);
        var timedOutSessions = sessionRepository.findAllByLastInteractionLessThan(timeout);
        sessionRepository.deleteAll(timedOutSessions);
    }
}
