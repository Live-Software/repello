package org.live.repello.menu;

import java.util.Collections;
import java.util.List;

public class RoomCreationMenu implements Menu{
    @Override
    public List<Menu> options() {
        return Collections.emptyList();
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
