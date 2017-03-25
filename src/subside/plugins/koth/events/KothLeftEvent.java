package subside.plugins.koth.events;

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
public class KothLeftEvent extends AbstractEvent implements Cancellable {
    private @Getter Capper<?> capper;
    private boolean isCancelled;
    private @Getter int amountSecondsCapped;
    private @Getter @Setter Capper<?> nextCapper;
    private @Getter RunningKoth runningKoth;
    private @Getter Capable captureZone;
    
    public KothLeftEvent(RunningKoth runningKoth, Capable captureZone, Capper<?> capper, int amountSecondsCapped){
        super(runningKoth.getKoth());
        this.runningKoth = runningKoth;
        this.captureZone = captureZone;
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
