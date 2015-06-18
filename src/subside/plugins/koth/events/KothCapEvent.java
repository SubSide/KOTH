package subside.plugins.koth.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import subside.plugins.koth.area.Area;

public class KothCapEvent extends AbstractEvent implements Cancellable {
    private String nextCapper;
    private List<Player> playersInArea;
    private boolean isCancelled;
    
    public KothCapEvent(Area area, List<Player> playersInArea, String nextCapper){
        super(area);
        this.playersInArea = playersInArea;
        this.nextCapper = nextCapper;
    }
    
    public String getNextPlayerCapping(){
        return nextCapper;
    }
    
    public void setNextPlayerCapping(String nextCapper){
        this.nextCapper = nextCapper;
    }
    
    public List<Player> getPlayersInArea(){
        return playersInArea;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
