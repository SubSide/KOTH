package subside.plugins.koth.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import subside.plugins.koth.area.Area;

public class KothOpenChestEvent extends AbstractEvent implements Cancellable {

    private Player player;
    private boolean isCancelled;
    
    public KothOpenChestEvent(Area area, Player player) {
        super(area);
        this.player = player;
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

}
