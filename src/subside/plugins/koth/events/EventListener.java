package subside.plugins.koth.events;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
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

import subside.plugins.koth.AbstractModule;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.loot.Loot;
import subside.plugins.koth.utils.Lang;
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

        if (!(e.getInventory().getHolder() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) e.getInventory().getHolder();
        Location loc = chest.getLocation();
        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            try {
                Location vec = koth.getLootPos();
                if (vec == null || loc.getWorld() != vec.getWorld() || loc.getBlockX() != vec.getBlockX() || loc.getBlockY() != vec.getBlockY() || loc.getBlockZ() != vec.getBlockZ())
                    continue;
    
                KothOpenChestEvent event = new KothOpenChestEvent(koth, (Player) e.getPlayer());
                event.setCancelled(true);
                try {
                    if(Perm.Admin.BYPASS.has((Player) e.getPlayer()) || (plugin.getConfigHandler().getKoth().isFfaChestTimeLimit() && koth.getLastWinner() == null && koth.getRunningKoth() == null) || (koth.getLastWinner() != null && koth.getLastWinner().isInOrEqualTo((Player)e.getPlayer()))){
                        event.setCancelled(false);
                    }
                } catch(Exception f){
                    plugin.getLogger().log(Level.WARNING, "Whoops, something went wrong, please contact the developer!", f);
                }
                
                Bukkit.getServer().getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    e.setCancelled(false);
                    return;
                }
                
                e.setCancelled(true);
            
            } catch(Exception ex){
                ex.printStackTrace();
            }

        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Perm.Admin.BYPASS.has((Player) e.getPlayer())) return;

        Location loc = e.getBlock().getLocation();
        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            Location vec = koth.getLootPos();
            if (vec == null) continue;
            if (loc.getWorld() == vec.getWorld() && loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ()) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Perm.Admin.BYPASS.has((Player) e.getPlayer())) return;
        Location loc = e.getBlock().getLocation();
        for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
            Location vec = koth.getLootPos();
            if (vec == null) continue;
            if (loc.getWorld() == vec.getWorld() && loc.getBlockX() == vec.getBlockX() && loc.getBlockY() == vec.getBlockY() && loc.getBlockZ() == vec.getBlockZ()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryChange(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player) || Perm.Admin.LOOT.has((Player)event.getWhoClicked())){
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
        if (!Perm.Admin.LOOT.has((Player) event.getPlayer())) {
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
