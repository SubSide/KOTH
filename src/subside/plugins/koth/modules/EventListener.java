package subside.plugins.koth.modules;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.events.KothOpenChestEvent;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.loot.Loot;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;

public class EventListener extends AbstractModule implements Listener {
    
    public EventListener(KothPlugin plugin){
        super(plugin);
    }
    
    @Override
    public void onEnable(){
        // Register the events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public void onDisable(){
        // Remove all previous event handlers
        HandlerList.unregisterAll(this);
    }
    
    
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        // We want to only filter single and double chests
        Location loc;
        if(e.getInventory().getHolder() instanceof Chest){
            loc = ((Chest) e.getInventory().getHolder()).getLocation();
        } else if(e.getInventory().getHolder() instanceof DoubleChest){
            loc = ((DoubleChest) e.getInventory().getHolder()).getLocation();
        } else {
            // So if it's neither of those we want to stop immediately
            return;
        }

        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            // We check if the chest we access is the actual loot chest
            // This function checks for both single and double chests
            if (!koth.isLootChest(loc))
                continue;

            // Create a new event and set the event on cancelled on default
            KothOpenChestEvent event = new KothOpenChestEvent(koth, (Player) e.getPlayer());
            event.setCancelled(true);
            try {
                // Does the player bypass this with a permission
                boolean hasBypass = Perm.Admin.BYPASS.has(e.getPlayer());
                // Do we have FFA enabled? If so, are we sure we didn't have a winner and that the koth isn't still running?
                boolean timeLimitTriggered = plugin.getConfigHandler().getKoth().isFfaChestTimeLimit() && koth.getLastWinner() == null && koth.getRunningKoth() == null;
                // Or is the player that is opening the chest the winner? Make sure lastWinner isn't null against NPE's.
                boolean isWinner = koth.getLastWinner() != null && koth.getLastWinner().isInOrEqualTo((Player)e.getPlayer());

                // Now check against all of the above, and if one of those is true, we should be able to open the chest
                // So in that case we set cancelled on false
                if(hasBypass || timeLimitTriggered || isWinner){
                    event.setCancelled(false);
                }
            } catch(Exception f){
                plugin.getLogger().log(Level.WARNING, "Whoops, something went wrong, please contact the developer!", f);
            }

            // We trigger our custom events so other plugins will have a say in what happens
            Bukkit.getServer().getPluginManager().callEvent(event);
            // If our custom event is not cancelled we allow the player to open the chest
            if(!event.isCancelled()){
                e.setCancelled(false);
                return;
            }

            // And else we cancel the opening of the chest
            e.setCancelled(true);


        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Perm.Admin.BYPASS.has(e.getPlayer())) return;

        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            if (koth.isLootChest(e.getBlock().getLocation())) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Perm.Admin.BYPASS.has(e.getPlayer())) return;

        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            if (koth.isLootChest(e.getBlock().getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryChange(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player) || Perm.Admin.LOOT.has(event.getWhoClicked())){
            return;
        }
        
        for(Loot loot : plugin.getLootHandler().getLoots()){
            if(event.getInventory().equals(loot.getInventory())){
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!Perm.Admin.LOOT.has(event.getPlayer())) {
            return;
        }
        
        for (Loot loot : plugin.getLootHandler().getLoots()) {
            if (event.getInventory().equals(loot.getInventory())) {
                plugin.getLootHandler().save();
                return;
            }
        }

    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(plugin.getKothHandler().getRunningKoth() != null){
            RunningKoth koth = plugin.getKothHandler().getRunningKoth();
            new MessageBuilder(Lang.KOTH_PLAYING_PLAYER_JOINING).koth(koth.getKoth()).buildAndSend(event.getPlayer());
        }
    }
}
