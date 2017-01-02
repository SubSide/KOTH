package subside.plugins.koth.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.gamemodes.RunningKoth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothPreUpdateEvent extends AbstractEvent implements Cancellable {
    private @Getter RunningKoth runningKoth;
    private boolean cancelled = false;
    
    public KothPreUpdateEvent(RunningKoth runningKoth){
        super(runningKoth.getKoth());
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
        return cancelled;
    }


    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
