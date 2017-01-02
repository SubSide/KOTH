package subside.plugins.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.KothPlugin.LoadingState;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class KothPluginInitializationEvent extends Event {
    
    private @Getter LoadingState loadingState;
    private @Getter KothPlugin plugin;
    
    public KothPluginInitializationEvent(KothPlugin plugin, LoadingState loadingState){
        this.plugin = plugin;
        this.loadingState = loadingState;
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
