package subside.plugins.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.gamemodes.RunningKoth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothPostUpdateEvent extends Event implements IEvent {
    private @Getter Koth koth;
    private @Getter RunningKoth runningKoth;
    
    public KothPostUpdateEvent(RunningKoth runningKoth){
        this.runningKoth = runningKoth;
        this.koth = runningKoth.getKoth();
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
