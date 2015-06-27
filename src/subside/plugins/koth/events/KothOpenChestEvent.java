package subside.plugins.koth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.KothDummy;
import subside.plugins.koth.area.Area;

public class KothOpenChestEvent extends Event implements IEvent, Cancellable {

    private Player player;
    private boolean isCancelled;
    private KothDummy koth;
    
    public KothOpenChestEvent(Area area, Player player) {
        this.koth = new KothDummy(area);
        this.player = player;
    }
    
    public KothDummy getKoth(){
        return koth;
    }
    
    
    public Player getPlayer(){
        return player;
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
