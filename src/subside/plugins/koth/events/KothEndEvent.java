package subside.plugins.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.KothDummy;
import subside.plugins.koth.area.Area;

public class KothEndEvent extends Event implements IEvent {
    private String winner;
    private boolean createChest;
    private KothDummy koth;
    
    
    public KothEndEvent(Area area, String capper){
        this.koth = new KothDummy(area);
        this.winner = capper;
        this.createChest = true;
    }
    
    public KothDummy getKoth(){
        return koth;
    }
    
    public String getPlayerCapping(){
        return winner;
    }
    
    public boolean isCreatingChest(){
        return createChest;
    }
    
    public void setChestCreation(boolean shouldCreate){
        createChest = shouldCreate;
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
