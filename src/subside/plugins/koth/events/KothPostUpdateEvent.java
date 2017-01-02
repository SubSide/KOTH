package subside.plugins.koth.events;

import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.gamemodes.RunningKoth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothPostUpdateEvent extends AbstractEvent {
    private @Getter RunningKoth runningKoth;
    
    public KothPostUpdateEvent(RunningKoth runningKoth){
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
}
