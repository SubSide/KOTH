package subside.plugins.koth.hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.modules.ConfigHandler.Hooks.Featherboard;

public class FeatherboardHook extends AbstractRangeHook {
    private @Getter @Setter boolean enabled = false;
    private String board;
    
    public FeatherboardHook(HookManager hookManager) {
        super(hookManager, 
                hookManager.getPlugin().getConfigHandler().getHooks().getFeatherboard().getRange(), 
                hookManager.getPlugin().getConfigHandler().getHooks().getFeatherboard().getRangeMargin());
        
        Featherboard fbHook = hookManager.getPlugin().getConfigHandler().getHooks().getFeatherboard();
        
        enabled = fbHook.isEnabled();
        board = fbHook.getBoard();
        
        getPlugin().getLogger().log(Level.INFO, "Featherboard hook: "+(enabled?"Enabled":"Disabled"));
        
    }

    @Override
    public void entersRange(Player player) {
        // Set board
        // To reduce randomly creating threads we check if it is the primary thread or otherwise create a sync thread
        if(Bukkit.isPrimaryThread()){
            be.maximvdw.featherboard.api.FeatherBoardAPI.showScoreboard(player, board);
        } else {
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
                @Override
                public void run() {
                    be.maximvdw.featherboard.api.FeatherBoardAPI.showScoreboard(player, board);
                }
            }, 1);
        }
        
    }

    @Override
    public void leavesRange(Player player) {
        // Reset board
        // To reduce randomly creating threads we check if it is the primary thread or otherwise create a sync thread
        if(Bukkit.isPrimaryThread()){
            be.maximvdw.featherboard.api.FeatherBoardAPI.removeScoreboardOverride(player, board);
            be.maximvdw.featherboard.api.FeatherBoardAPI.resetDefaultScoreboard(player);
        } else {
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
                @Override
                public void run() {
                    be.maximvdw.featherboard.api.FeatherBoardAPI.removeScoreboardOverride(player, board);
                    be.maximvdw.featherboard.api.FeatherBoardAPI.resetDefaultScoreboard(player);
                }
            }, 1);
        }
    }
    
}
