package org.live.lobbyserver.model.player;

import java.util.Optional;

public interface PlayerRepository {

    Optional<Player> findById(String id);

    void save(Player player);
}
