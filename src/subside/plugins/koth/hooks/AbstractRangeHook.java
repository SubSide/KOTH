package subside.plugins.koth.hooks;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothInitializeEvent;
import subside.plugins.koth.gamemodes.RunningKoth;
import com.google.common.collect.Sets;

public abstract class AbstractRangeHook extends AbstractHook implements Listener {
    private @Getter RunningKoth koth;
    
    private @Getter int range;
    private @Getter int rangeMargin;
    private Set<OfflinePlayer> inRange;
    
    /**
     * The constructor!
     * 
     * @param hookManager the parent HookManager
     * @param range the range it should toggle
     * @param rangeMargin the margin it should use to block annoying switching
     */
    public AbstractRangeHook(HookManager hookManager, int range, int rangeMargin){
        super(hookManager); // First call the constructor of the parent class
        
        inRange = new HashSet<>();
        
        this.range = range;
        this.rangeMargin = rangeMargin;
    }
    
    /**
     * Use this when a KoTH gets initialized
     */
    public void initialize(RunningKoth koth){}
    
    /**
     * Use this when a KoTH gets uninitialized
     */
    public void uninitialize(){}
    
    /**
     * Update method so we don't override the tick method
     */
    public void update(){}
    
    /**
     * This is called when the player enters the range.
     * 
     * @param player the player that enters the range
     */
    public abstract void entersRange(Player player);
    
    /**
     * This is called when the player leaves the range.
     * 
     * @param player the player that leaves the range
     */
    public abstract void leavesRange(Player player);
    
    /**
     * When a KoTH gets initialized we want to check players in range.
     * The reason we want to use KothInitializeEvent instead of KothStartEvent
     * is that KothStartEvent isn't triggered on reload in combination with the cache system
     * 
     * @param event the KothInitializeEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onKothInitialize(KothInitializeEvent event){
        if(koth != null) return; // If we are already tracking a KoTH, stop.
        
        koth = event.getRunningKoth();
        
        // Initialize everything (if we need to)
        initialize(event.getRunningKoth());
        
        // We want to delay the addition of players so other plugins can be properly initialized
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()){
                if(isInRange(player, true)){
                    addPlayer(player);
                }
            }
        }, 5);
    }

    /**
     * When the KoTH ends we want to reset everyone
     * 
     * @param event the KothEndEvent
     */
    @EventHandler
    public void onKothEnd(KothEndEvent event){
        if(koth == null) return; // If we aren't tracking a KoTH, stop.
        resetAll();
        
        uninitialize();
    }
    
    /**
     * When the plugin gets disabled (through reload or /koth reload)
     * we want to reset everyone
     */
    @Override
    public void onDisable(){
        if(koth == null) return;
        resetAll();

        uninitialize();
    }
    
    /**
     * When a player joins we want to check if the player is in range, and if so,
     * add the player to the list
     * @param event the PlayerJoinEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event){
        updatePlayer(event.getPlayer());
    }
    
    /**
     * When a player leaves, remove the player.
     * 
     * @param event the PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        removePlayer(event.getPlayer());
    }
    
    /**
     * I remember a point where when a player is kicked, it didn't trigger the PlayerQuitEvent
     * so just to make sure, we handle the PlayerKickEvent as well.
     * 
     * @param event the PlayerKickEvent
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event){
        removePlayer(event.getPlayer());
    }
    
    /**
     * The main tick function. Which we won't use if the range is unlimited.
     */
    @Override
    public final void tick(){
        if(range < 0 || koth == null) return;
        
        // Run the update for extending classes
        update();
        
        for(Player player : Bukkit.getOnlinePlayers()){
            updatePlayer(player);
        }
    }
    
    /**
     * Reset all players. Triggered when the KoTH ends.
     */
    public final void resetAll(){
        for(OfflinePlayer player : Sets.newHashSet(inRange)){
            if(player.isOnline())
                removePlayer((Player) player);
        }
        inRange.clear();
        koth = null;
    }
    
    /**
     * We use this to add a player to the list and to trigger entersRange
     * @param player the Player that gets added
     */
    private final void addPlayer(Player player){
        if(containsPlayer(player)) return;
        
        inRange.add(player);
        entersRange(player);
    }
    
    /**
     * We use this to remove a player from the list and to trigger leavesRange
     * @param player the player that gets removed
     */
    private final void removePlayer(Player player){
        if(!containsPlayer(player)) return;
        
        inRange.remove(player);
        leavesRange(player);
    }
    
    /**
     * A simple method we use to check if the list contains a player.
     * @param player the player to check
     *
     * @return true if the player is in the list
     */
    public final boolean containsPlayer(Player player){
        return inRange.contains(player);
    }
    
    /**
     * Main update method for a player
     * 
     * @param player the player to update
     */
    private final void updatePlayer(Player player){
        boolean wasInRange = containsPlayer(player);
        boolean isInRange = isInRange(player, wasInRange);
        
        if(!wasInRange && isInRange){
            addPlayer(player);
        } else if(wasInRange && !isInRange){
            removePlayer(player);
        }
    }
    
    /**
     * We use this function to see if the player is in range or not.
     *
     * @param player the player to check
     * @param wasInRange if the player was already in range
     *
     * @return true if the Player is in range
     */
    public final boolean isInRange(Player player, boolean wasInRange){
        if(koth == null) return false; // You can't be in range if there's no KoTH running
        if(range < 0) return true; // Always return true if the range is -1 (or lower)
        if(!player.isOnline()) return false; // He's not online!
        
        Location loc = koth.getKoth().getMiddle();
        
        if(wasInRange){
            // If the player was already in range, we accept the range + rangeMargin to stay in range.
            if(loc.getWorld() == player.getLocation().getWorld() && loc.distance(player.getLocation()) <= range+rangeMargin){
                return true;
            }
        } else {
            // If the player was not yet in range, we accept the range - rangeMargin to stay in range.
            if(loc.getWorld() == player.getLocation().getWorld() && loc.distance(player.getLocation()) <= range-rangeMargin){
                return true;
            }
        }
        
        return false;
    }
    
}
