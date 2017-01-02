package subside.plugins.koth.events;

import org.bukkit.event.Event;

import lombok.Getter;
import subside.plugins.koth.areas.Koth;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public abstract class AbstractEvent extends Event {
    private @Getter Koth koth;
    
    public AbstractEvent(Koth koth){
        this.koth = koth;
    }
}
