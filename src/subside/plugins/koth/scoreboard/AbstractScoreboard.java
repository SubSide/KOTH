package subside.plugins.koth.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import subside.plugins.koth.adapter.RunningKoth;

public abstract class AbstractScoreboard implements Listener {
    public abstract void updateScoreboard();
    public abstract void playerQuit(Player player);
    public abstract void load(RunningKoth koth, String titleLoader, String[] textLoader);
    public abstract void destroy();
    
}
