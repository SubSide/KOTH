package subside.plugins.koth.events;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Area;

public class KothOpenChestEvent extends Event implements IEvent, Cancellable {

    private @Getter Player player;
    private boolean isCancelled;
    private @Getter Area area;
    
    public KothOpenChestEvent(Area area, Player player) {
        this.area = area;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
