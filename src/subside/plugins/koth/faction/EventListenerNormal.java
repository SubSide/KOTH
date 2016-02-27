package subside.plugins.koth.faction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;

import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.events.KothOpenChestEvent;

public class EventListenerNormal implements Listener {
    @SuppressWarnings("unused")
    private boolean capAllAreas;
    
    public EventListenerNormal(boolean capAllAreas){
        this.capAllAreas = capAllAreas;
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCap(KothCapEvent event) {
        String fName = MPlayer.get(Bukkit.getPlayer(event.getNextPlayerCapping())).getFactionName();
        if (fName != null && !fName.equalsIgnoreCase("") && !fName.endsWith("Wilderness")) {
            event.setNextPlayerCapping(fName);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKothLeft(KothLeftEvent event) {
        Faction prevFaction = FactionColl.get().getByName(event.getCapper());
        if (prevFaction != null) {
            for (Player player : prevFaction.getOnlinePlayers()) {
                if (event.getKoth().isInArea(player)) {
                    if (MPlayer.get(player).getFactionId().equalsIgnoreCase(prevFaction.getId())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKothOpenChest(KothOpenChestEvent event) {
        if (MPlayer.get(event.getPlayer()).getFactionName().equalsIgnoreCase(event.getKoth().getLastWinner())) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }

    }
}
