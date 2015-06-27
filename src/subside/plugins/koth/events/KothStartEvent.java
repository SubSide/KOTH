package subside.plugins.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.KothDummy;
import subside.plugins.koth.area.Area;

public class KothStartEvent extends Event implements IEvent {
    private int length;
    private KothDummy koth;
    
    public KothStartEvent(Area area, int length){
        this.koth = new KothDummy(area);
        this.length = length;
    }
    
    public KothDummy getKoth(){
        return koth;
    }
    
    
    public int getLengthInSeconds(){
        return length;
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
