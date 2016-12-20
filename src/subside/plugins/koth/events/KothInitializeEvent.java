package subside.plugins.koth.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.RunningKoth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothInitializeEvent extends Event implements IEvent, Cancellable {
    private @Getter RunningKoth runningKoth;
    
    private boolean isCancelled;
    
    public KothInitializeEvent(RunningKoth runningKoth){
        this.runningKoth = runningKoth;
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


    @Override
    public Koth getKoth() {
        return runningKoth.getKoth();
    }
}
