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
public class KothLeftEvent extends Event implements IEvent, Cancellable {
    private @Getter String capper;
    private boolean isCancelled;
    private @Getter int amountSecondsCapped;
    private @Getter @Setter String nextCapper;
    private @Getter Koth koth;
    
    public KothLeftEvent(Koth koth, String capper, int amountSecondsCapped){
        this.koth = koth;
        this.capper = capper;
        this.amountSecondsCapped = amountSecondsCapped;
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
