package org.live.lobbyserver.model.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.live.lobbyserver.model.session.Session;

@Getter
@Setter
@NoArgsConstructor
public class Player {

    private String id;
    private Session session;

    public Player(String id) {
        this.id = id;
    }

}
