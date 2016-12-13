package subside.plugins.koth.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.adapter.RunningKoth;

public abstract class AbstractScoreboard implements Listener {
    public abstract void updateScoreboard();
    public abstract void playerQuit(Player player);
    public abstract void load(RunningKoth koth, String titleLoader, String[] textLoader);
    public abstract void destroy();
    
    public String chop(String str){
        int maxL = ConfigHandler.getInstance().getScoreboard().getCharacterLimit();
        int maxLength = (str.length() < maxL)?str.length():maxL;
        return str.substring(0, maxLength);
    }
    public String chopTitle(String str){
        int maxL = ConfigHandler.getInstance().getScoreboard().getCharacterTitleLimit();
        int maxLength = (str.length() < maxL)?str.length():maxL;
        return str.substring(0, maxLength);
    }
}
