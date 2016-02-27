package subside.plugins.koth.events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothStartEvent extends Event implements IEvent, Cancellable {
    private @Getter @Setter int captureTime;
    private @Getter @Setter int maxLength;
    private @Getter Koth koth;
    private @Getter boolean scheduled;
    
    private boolean isCancelled;
    
    public KothStartEvent(Koth koth, int captureTime, int maxLength, boolean scheduled){
        this.koth = koth;
        this.captureTime = captureTime;
        this.maxLength = maxLength;
        this.scheduled = scheduled;
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
