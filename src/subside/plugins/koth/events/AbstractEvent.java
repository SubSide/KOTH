package subside.plugins.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.area.Area;

public abstract class AbstractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private KothDummy dummy;
    
    public AbstractEvent(Area area){
        dummy = new KothDummy(area);
    }
    
    public KothDummy getKoth(){
        return dummy;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}