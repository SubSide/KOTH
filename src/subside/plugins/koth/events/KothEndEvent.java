package subside.plugins.koth.events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothEndEvent extends Event implements IEvent {
    private @Getter String winner;
    private @Getter @Setter boolean creatingChest;
    private @Getter Koth koth;
    
    
    public KothEndEvent(Koth koth, String capper){
        this.koth = koth;
        this.winner = capper;
        this.creatingChest = true;
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
