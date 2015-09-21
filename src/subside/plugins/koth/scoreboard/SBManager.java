package subside.plugins.koth.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SBManager implements Listener {
    private boolean useOld = false;
    private static SBManager sbm = new SBManager();
    
    public void load(boolean useOld, String titleLoader, String[] textLoader){
        this.useOld = useOld;
        if(useOld){
            OldScoreboardHandler.load(titleLoader, textLoader);
        } else {
            ScoreboardHandler.load(titleLoader, textLoader);
        }
    }
    
    public void clearAll(){
        if(useOld){
            OldScoreboardHandler.clearAll();
        } else {
            ScoreboardHandler.clearSB();
        }
    }
    
    public void clearPlayer(Player player){
        if(useOld){
            OldScoreboardHandler.clearPlayer(player);
        }
    }
    
    public void update(){
        if(useOld){
            OldScoreboardHandler.updateScoreboard();
        } else {
            ScoreboardHandler.updateScoreboard();
        }
    }
    
    public static SBManager getManager(){
        return sbm;
    }
    
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearPlayer(event.getPlayer());
    }
}
