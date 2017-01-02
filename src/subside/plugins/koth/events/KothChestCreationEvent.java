package subside.plugins.koth.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.areas.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothChestCreationEvent extends AbstractEvent implements Cancellable {
    private boolean isCancelled;
    private @Getter @Setter ItemStack[] loot;
    
    public KothChestCreationEvent(Koth koth, ItemStack[] loot){
        super(koth);
        this.loot = loot;
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
