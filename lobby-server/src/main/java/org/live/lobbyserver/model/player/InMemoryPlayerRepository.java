package org.live.lobbyserver.model.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPlayerRepository implements PlayerRepository{

    private final Map<String, Player> players;

    public InMemoryPlayerRepository() {
        players = new HashMap<>();
    }

    @Override
    public Optional<Player> findById(String id) {
        return Optional.ofNullable(players.get(id));
    }

    @Override
    public void save(Player player) {
        players.put(player.getId(), player);
    }
}
