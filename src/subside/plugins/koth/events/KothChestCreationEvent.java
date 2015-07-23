package subside.plugins.koth.events;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import subside.plugins.koth.adapter.KothDummy;
import subside.plugins.koth.area.Area;

public class KothChestCreationEvent extends Event implements IEvent, Cancellable {
    private boolean isCancelled;
    private @Getter KothDummy koth;
    private @Getter @Setter ItemStack[] loot;
    
    public KothChestCreationEvent(Area koth, ItemStack[] loot){
        this.koth = new KothDummy(koth);
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
