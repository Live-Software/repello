package org.live.lobbyserver.session;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.live.lobbyserver.model.player.Player;
import org.live.lobbyserver.model.player.PlayerRepository;
import org.live.lobbyserver.model.session.Session;
import org.live.lobbyserver.model.session.SessionRepository;
import org.live.lobbyserver.util.DateUtil;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionManagerTest {

    private static final int SESSION_TIMEOUT_SECONDS = 300;

    private SessionManager sessionManager;
    private SessionRepository mockSessionRepository;
    private PlayerRepository mockPlayerRepository;
    private DateUtil mockDateUtil;

    @BeforeEach
    public void setUp() {
        mockDateUtil = mock(DateUtil.class);
        mockSessionRepository = mock(SessionRepository.class);
        mockPlayerRepository = mock(PlayerRepository.class);

        sessionManager = new SessionManager(
                mockDateUtil,
                mockSessionRepository,
                mockPlayerRepository,
                SESSION_TIMEOUT_SECONDS);
    }

    @Test
    public void addPlayerToSession_createsNewSession_withNewPlayer() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.empty());
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.empty());

        sessionManager.addPlayerToSession(sessionCode, playerId);

        var sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(mockSessionRepository, times(1)).save(sessionCaptor.capture());

        var savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getId()).isEqualTo(sessionCode);
        assertThat(savedSession.getPlayers()).hasSize(1);
        assertThat(savedSession.getPlayers().get(0).getId()).isEqualTo(playerId);

        var playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(mockPlayerRepository, times(1)).save(playerCaptor.capture());

        var savedPlayer = playerCaptor.getValue();
        assertThat(savedPlayer.getId()).isEqualTo(playerId);
        assertThat(savedPlayer.getSession().getId()).isEqualTo(sessionCode);
    }

    @Test
    public void addPlayerToSession_createsNewSession_withExistingPlayer() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingPlayer = new Player(playerId);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.empty());

        sessionManager.addPlayerToSession(sessionCode, playerId);

        var sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(mockSessionRepository, times(1)).save(sessionCaptor.capture());

        var savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getId()).isEqualTo(sessionCode);
        assertThat(savedSession.getPlayers()).hasSize(1);
        assertThat(savedSession.getPlayers().get(0).getId()).isEqualTo(playerId);

        verify(mockPlayerRepository, times(1)).save(existingPlayer);
        assertThat(existingPlayer.getSession()).isEqualTo(savedSession);
    }

    @Test
    public void addPlayerToSession_addsNewPlayer_toExistingSession() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingSession = new Session(sessionCode);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.empty());
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.of(existingSession));

        sessionManager.addPlayerToSession(sessionCode, playerId);

        var playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(mockPlayerRepository, times(1)).save(playerCaptor.capture());

        var savedPlayer = playerCaptor.getValue();
        assertThat(savedPlayer.getId()).isEqualTo(playerId);
        assertThat(savedPlayer.getSession().getId()).isEqualTo(sessionCode);

        verify(mockSessionRepository, times(1)).save(existingSession);
        assertThat(existingSession.getPlayers()).hasSize(1);
        assertThat(existingSession.getPlayers().get(0)).isEqualTo(savedPlayer);
    }

    @Test
    public void addPlayerToSession_addsExistingPlayer_toExistingSession() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingPlayer = new Player(playerId);
        var existingSession = new Session(sessionCode);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.of(existingSession));

        sessionManager.addPlayerToSession(sessionCode, playerId);

        verify(mockSessionRepository, times(1)).save(existingSession);
        assertThat(existingSession.getPlayers()).hasSize(1);
        assertThat(existingSession.getPlayers().get(0)).isEqualTo(existingPlayer);

        verify(mockPlayerRepository, times(1)).save(existingPlayer);
        assertThat(existingPlayer.getSession()).isEqualTo(existingSession);
    }

    @Test
    public void addPlayerToSession_addsPlayerToSession_withPlayersAlreadyInSession() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingSession = new Session(sessionCode);
        existingSession.getPlayers().add(new Player());
        existingSession.getPlayers().add(new Player());

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.empty());
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.of(existingSession));

        sessionManager.addPlayerToSession(sessionCode, playerId);

        var sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(mockSessionRepository, times(1)).save(sessionCaptor.capture());

        var savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getId()).isEqualTo(sessionCode);
        assertThat(savedSession.getPlayers()).hasSize(3);
    }

    @Test
    public void disconnectPlayer_removesExistingPlayer_fromExistingSession() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingPlayer = new Player(playerId);
        var existingSession = new Session(sessionCode);
        existingSession.getPlayers().add(existingPlayer);
        existingPlayer.setSession(existingSession);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.of(existingSession));

        sessionManager.disconnectPlayer(playerId);

        verify(mockSessionRepository, times(1)).save(existingSession);
        assertThat(existingSession.getPlayers()).isEmpty();
    }

    @Test
    public void disconnectPlayer_doesNotRemove_existingPlayer_fromNonExistentSession() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingPlayer = new Player(playerId);
        var existingSession = new Session(sessionCode);
        existingPlayer.setSession(existingSession);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.empty());

        sessionManager.disconnectPlayer(playerId);

        verify(mockSessionRepository, never()).save(any());
    }

    @Test
    public void disconnectPlayer_doesNotRemove_nonExistentPlayer() {
        var playerId = "PlayerOne";
        var sessionCode = "sessionOne";

        var existingSession = new Session(sessionCode);

        when(mockPlayerRepository.findById(playerId)).thenReturn(Optional.empty());
        when(mockSessionRepository.findById(sessionCode)).thenReturn(Optional.of(existingSession));

        sessionManager.disconnectPlayer(playerId);

        verify(mockSessionRepository, never()).save(any());
        assertThat(existingSession.getPlayers()).isEmpty();
    }

    @Test
    public void removeUnusedSessions_removesTimedOutSessions() {
        var currentTime = Instant.parse("2022-02-02T22:22:22Z");
        var sessionList = List.of(new Session("one"), new Session("two"), new Session("three"));

        when(mockDateUtil.getCurrentTime()).thenReturn(currentTime);
        when(mockSessionRepository.findAllByLastInteractionLessThan(currentTime.minusSeconds(SESSION_TIMEOUT_SECONDS)))
                .thenReturn(sessionList);

        sessionManager.removeUnusedSessions();

        verify(mockSessionRepository, times(1)).deleteAll(sessionList);
    }

}