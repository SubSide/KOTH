package subside.plugins.koth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.areas.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothOpenChestEvent extends AbstractEvent implements Cancellable {

    private @Getter Player player;
    private boolean isCancelled;
    
    public KothOpenChestEvent(Koth koth, Player player) {
        super(koth);
        this.player = player;
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
