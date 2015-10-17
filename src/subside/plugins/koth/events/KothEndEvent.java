package subside.plugins.koth.events;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import subside.plugins.koth.adapter.Koth;

public class KothEndEvent extends Event implements IEvent {
    private String winner;
    private boolean createChest;
    private @Getter Koth koth;
    
    
    public KothEndEvent(Koth koth, String capper){
        this.koth = koth;
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
