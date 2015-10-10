package subside.plugins.koth.events;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Area;

public class KothEndEvent extends Event implements IEvent {
    private String winner;
    private boolean createChest;
    private @Getter Area area;
    
    
    public KothEndEvent(Area area, String capper){
        this.area = area;
        this.winner = capper;
        this.createChest = true;
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
