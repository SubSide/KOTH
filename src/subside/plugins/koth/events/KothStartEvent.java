package subside.plugins.koth.events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Koth;

public class KothStartEvent extends Event implements IEvent, Cancellable {
    private @Getter @Setter int length;
    private @Getter @Setter int maxLength;
    private @Getter Koth koth;
    private boolean isScheduled;
    
    private boolean isCancelled;
    
    public KothStartEvent(Koth koth, int length, int maxLength, boolean isScheduled){
        this.koth = koth;
        this.length = length;
        this.maxLength = maxLength;
        this.isScheduled = isScheduled;
    }
    
    public boolean isScheduled(){
        return isScheduled;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
