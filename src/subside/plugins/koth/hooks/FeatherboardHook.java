package subside.plugins.koth.hooks;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import subside.plugins.koth.KothPlugin.LoadingState;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothInitializeEvent;
import subside.plugins.koth.events.KothPluginInitializationEvent;
import subside.plugins.koth.modules.ConfigHandler.Hooks.Featherboard;

public class FeatherboardHook extends AbstractHook implements Listener {
    private @Getter boolean enabled = false;
    private Koth koth;
    
    
    private @Getter int range = 20;
    private @Getter int rangeMargin = 5;
    private @Getter String board;
    private List<OfflinePlayer> inRange;
    
    public FeatherboardHook(HookManager hookManager){
        super(hookManager); // First call the constructor of the parent class
        
        inRange = new ArrayList<>();
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("FeatherBoard")){
            Featherboard fbHook = getPlugin().getConfigHandler().getHooks().getFeatherboard();
            if(fbHook.isEnabled()){
                enabled = true;
                range = fbHook.getRange();
                rangeMargin = fbHook.getRangeMargin();
                board = fbHook.getBoard();
            }
        }
        getPlugin().getLogger().log(Level.INFO, "Featherboard hook: "+(enabled?"Enabled":"Disabled"));
    }
    
    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onKothInitialize(KothInitializeEvent event){
        if(!isEnabled()) return;
        koth = event.getKoth();
        if(range < 0){
            for(Player player : Bukkit.getOnlinePlayers()){
                inRange.add(player);
                forceSyncSetBoard(player);
            }
        }
    }

    @EventHandler
    public void onKothEnd(KothEndEvent event){
        if(!isEnabled() || koth == null) return;
        resetAll();
    }
    
    @EventHandler
    public void onKothPluginInitialization(KothPluginInitializationEvent event){
        if((!isEnabled() || koth == null) && event.getLoadingState() != LoadingState.DISABLE) return;
        
        resetAll();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!isEnabled() || koth == null) return;
        if(range < 0){
            inRange.add(event.getPlayer());
            setBoard(event.getPlayer(), board);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(!isEnabled()) return;
        resetBoard(event.getPlayer(), board);
    }

    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        if(!isEnabled()) return;
        resetBoard(event.getPlayer(), board);
    }
    
    
    @Override
    public void tick(){
        if(!isEnabled() || range < 0) return;
        
        if(koth == null || !koth.isRunning()){

            for(OfflinePlayer player : inRange){
                if(player.isOnline())
                    forceSyncSetBoard(player.getPlayer());
            }
            inRange.clear();
            koth = null;
            return;
        }
        
        for(Player player: Bukkit.getOnlinePlayers()){
            Location loc = koth.getMiddle();
            if(!inRange.contains(player)){
                if(loc.getWorld() == player.getLocation().getWorld() && loc.distance(player.getLocation()) <= range-rangeMargin){
                    inRange.add(player);
                    forceSyncSetBoard(player);
                }
            } else {
                if(loc.getWorld() != player.getLocation().getWorld() || loc.distance(player.getLocation()) >= range+rangeMargin){
                    inRange.remove(player);
                    forceSyncResetBoard(player);
                }
            }
        }
    }
    
    public void resetAll(){
        for(OfflinePlayer player : inRange){
            if(player.isOnline())
                forceSyncResetBoard(player.getPlayer());
        }
        inRange.clear();
        koth = null;
    }
    
    public void forceSyncSetBoard(final Player player){
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
            @Override
            public void run() {
                setBoard(player, board);
            }
        }, 1);
    }
    
    public void forceSyncResetBoard(final Player player){
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
            @Override
            public void run() {
                resetBoard(player, board);
            }
        }, 1);
    }

    
    public void setBoard(final Player player, final String board){
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
            @Override
            public void run() {
                be.maximvdw.featherboard.api.FeatherBoardAPI.showScoreboard(player, board);
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fb show "+player.getName()+" "+board);
            }
        }, 1);
    }
    
    public void resetBoard(final Player player, final String board){
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable(){
            @Override
            public void run() {
                be.maximvdw.featherboard.api.FeatherBoardAPI.removeScoreboardOverride(player, board);
                be.maximvdw.featherboard.api.FeatherBoardAPI.resetDefaultScoreboard(player);
                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fb hide "+player.getName()+" "+board);
            }
        }, 1);
    }
    
}
