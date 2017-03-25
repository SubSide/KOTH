package subside.plugins.koth.events;

import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothEndEvent extends AbstractEvent {
    private @Getter Capper<?> winner;
    private @Getter @Setter boolean triggerLoot;
    private @Getter EndReason reason;
    private @Getter RunningKoth runningKoth;
    
    
    public KothEndEvent(RunningKoth runningKoth, Capper<?> capper, EndReason reason){
        super(runningKoth.getKoth());
        this.runningKoth = runningKoth;
        this.winner = capper;
        this.triggerLoot = true;
        this.reason = reason;
    }
    
    @Deprecated
    public void setCreatingChest(boolean creatingChest){
        this.triggerLoot = creatingChest;
    }
    
    @Deprecated
    public boolean isCreatingChest(){
        return this.triggerLoot;
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
