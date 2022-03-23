package org.live.repello.menu;

import java.util.List;

public interface Menu {
    List<Menu> options();
    List<MenuAction> actions();
    Menu handleAction(MenuAction action);
}
