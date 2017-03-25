package subside.plugins.koth.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.areas.Capable;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.gamemodes.RunningKoth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothCapEvent extends AbstractEvent implements Cancellable {
    private @Getter @Setter Capper<?> nextCapper;
    private @Getter List<Player> playersInArea;
    private boolean isCancelled;
    private @Getter RunningKoth runningKoth;
    private @Getter Capable captureZone;
    
    public KothCapEvent(RunningKoth runningKoth, Capable captureZone, List<Player> playersInArea, Capper<?> nextCapper){
        super(runningKoth.getKoth());
        
        this.runningKoth = runningKoth;
        this.playersInArea = playersInArea;
        this.nextCapper = nextCapper;
        this.captureZone = captureZone;
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
