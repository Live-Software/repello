package org.live.repello.menu;

import java.util.Collections;
import java.util.List;

public class MainMenu implements Menu{

    private final RoomCreationMenu roomCreationMenu;
    private final JoinRoomMenu joinRoomMenu;

    public MainMenu(RoomCreationMenu roomCreationMenu, JoinRoomMenu joinRoomMenu) {
        this.roomCreationMenu = roomCreationMenu;
        this.joinRoomMenu = joinRoomMenu;
    }

    @Override
    public List<Menu> options() {
        return List.of(roomCreationMenu, joinRoomMenu);
    }

    @Override
    public List<MenuAction> actions() {
        return Collections.emptyList();
    }

    @Override
    public Menu handleAction(MenuAction action) {
        return this;
    }
}
