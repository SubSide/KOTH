package subside.plugins.koth.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.areas.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothStartEvent extends AbstractEvent implements Cancellable {
    private @Getter @Setter int captureTime;
    private @Getter @Setter int maxLength;
    private @Getter boolean scheduled;
    private @Getter @Setter String entityType;
    
    private boolean isCancelled;
    
    public KothStartEvent(Koth koth, int captureTime, int maxLength, boolean scheduled, String entityType){
        super(koth);
        this.captureTime = captureTime;
        this.maxLength = maxLength;
        this.scheduled = scheduled;
        this.entityType = entityType;
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
