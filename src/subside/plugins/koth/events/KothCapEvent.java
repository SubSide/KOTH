package subside.plugins.koth.events;

import java.util.List;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothCapEvent extends Event implements IEvent, Cancellable {
    private String nextCapper;
    private @Getter List<Player> playersInArea;
    private boolean isCancelled;
    private @Getter Koth koth;
    
    public KothCapEvent(Koth koth, List<Player> playersInArea, String nextCapper){
        this.koth = koth;
        this.playersInArea = playersInArea;
        this.nextCapper = nextCapper;
    }

    /** Get the next player that will cap this KoTH
     * 
     * @return          The next player that will cap this KoTH
     */
    public String getNextPlayerCapping(){
        return nextCapper;
    }

    /** The next player that will cap this KoTH
     * 
     * @param name      The next player that will cap this KoTH
     */
    public void setNextPlayerCapping(String nextCapper){
        this.nextCapper = nextCapper;
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
