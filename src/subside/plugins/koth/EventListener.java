package subside.plugins.koth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.Loot;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.events.KothOpenChestEvent;
import subside.plugins.koth.loaders.LootLoader;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Perm;
import subside.plugins.koth.utils.Utils;

public class EventListener implements Listener {
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
        for (Koth koth : KothHandler.getInstance().getAvailableKoths()) {
            try {
                Location vec = koth.getLootPos();
                if (vec == null || loc.getWorld() != vec.getWorld() || loc.getBlockX() != vec.getBlockX() || loc.getBlockY() != vec.getBlockY() || loc.getBlockZ() != vec.getBlockZ())
                    continue;
    
                KothOpenChestEvent event = new KothOpenChestEvent(koth, (Player) e.getPlayer());
                event.setCancelled(true);
                try {
                    if(Perm.Admin.BYPASS.has((Player) e.getPlayer()) || (koth.getLastWinner() != null && koth.getLastWinner().isInOrEqualTo((Player)e.getPlayer()))){
                        event.setCancelled(false);
                    }
                } catch(Exception f){
                    Utils.log("Whoops, something went wrong, please contact the developer!");
                    f.printStackTrace();
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
        for (Koth koth : KothHandler.getInstance().getAvailableKoths()) {
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
        for (Koth koth : KothHandler.getInstance().getAvailableKoths()) {
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
        
        for(Loot loot : KothHandler.getInstance().getLoots()){
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
        
        for (Loot loot : KothHandler.getInstance().getLoots()) {
            if (event.getInventory().equals(loot.getInventory())) {
                LootLoader.save();
                return;
            }
        }

    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(KothHandler.getInstance().getRunningKoth() != null){
            RunningKoth koth = KothHandler.getInstance().getRunningKoth();
            new MessageBuilder(Lang.KOTH_PLAYING_PLAYER_JOINING).koth(koth.getKoth()).buildAndSend(event.getPlayer());
        }
    }
}
