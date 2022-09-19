package me.CubeLegend.TheManHunt.StateSystem;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameState changeFrom;
    private GameState changeTo;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GameStateChangeEvent(GameState changeFrom, GameState changeTo) {
        this.changeFrom = changeFrom;
        this.changeTo = changeTo;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public GameState getChangeFrom() {
        return changeFrom;
    }

    public GameState getChangeTo() {
        return changeTo;
    }

    public void setChangeTo(GameState changeTo) {
        this.changeTo = changeTo;
    }
}
