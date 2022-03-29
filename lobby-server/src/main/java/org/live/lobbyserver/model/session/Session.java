package org.live.lobbyserver.model.session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.live.lobbyserver.model.player.Player;

@Getter
@Setter
@NoArgsConstructor
public class Session {

    private String id;
    private List<Player> players = new ArrayList<>();
    private Instant lastInteraction = Instant.now();

    public Session(String id) {
        this.id = id;
    }

}
