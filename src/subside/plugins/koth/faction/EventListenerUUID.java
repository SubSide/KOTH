package subside.plugins.koth.faction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import subside.plugins.koth.adapter.Area;
import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.events.KothOpenChestEvent;

public class EventListenerUUID implements Listener {
    
    private boolean capAllAreas;
    
    public EventListenerUUID(boolean capAllAreas){
        this.capAllAreas = capAllAreas;
    }
    

    @EventHandler(ignoreCancelled = true)
    public void onKothCap(KothCapEvent event) {
        String fName = FPlayers.getInstance().getByPlayer(Bukkit.getPlayer(event.getNextPlayerCapping())).getFaction().getTag();
        if (fName != null && !fName.equalsIgnoreCase("") && !fName.endsWith("Wilderness")) {
            event.setNextPlayerCapping(fName);
        } else {
            event.setCancelled(true);
            return;
        }
        if (!this.capAllAreas) {
            Faction fac = FPlayers.getInstance().getByPlayer(Bukkit.getPlayer(event.getNextPlayerCapping())).getFaction();

            if (fName != null && !fName.equalsIgnoreCase("") && !fName.endsWith("Wilderness")) {
                event.setNextPlayerCapping(fName);
            } else {
                event.setCancelled(true);
            }

            List<Player> facPlayers = new ArrayList<>();
            for (Player player : (List<Player>) event.getPlayersInArea()) {
                if (FPlayers.getInstance().getByPlayer(player).getFaction() == fac) {
                    facPlayers.add(player);
                }
            }

            for (Area area : (List<Area>) event.getKoth().getAreas()) {
                boolean isInArea = false;
                for (Player player : facPlayers) {
                    if (area.isInArea(player)) {
                        isInArea = true;
                        break;
                    }
                }
                if (!isInArea) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onKothLeft(KothLeftEvent event) {
        Faction prevFaction = Factions.getInstance().getByTag(event.getCapper());
        if (prevFaction != null) {
            for (Player player : prevFaction.getOnlinePlayers()) {
                if (event.getKoth().isInArea(player)) {
                    if (FPlayers.getInstance().getByPlayer(player).getTag().equalsIgnoreCase(prevFaction.getTag())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (this.capAllAreas) {

            Set<FPlayer> facPlayers = prevFaction.getFPlayersWhereOnline(true);

            for (Area area : (List<Area>) event.getKoth().getAreas()) {
                boolean isInArea = false;
                for (FPlayer player : facPlayers) {
                    if (area.isInArea(player.getPlayer())) {
                        isInArea = true;
                        break;
                    }
                }
                if (!isInArea) {
                    event.setCancelled(false);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onKothOpenChest(KothOpenChestEvent event) {
        if (FPlayers.getInstance().getByPlayer(event.getPlayer()).getTag().equalsIgnoreCase(event.getKoth().getLastWinner())) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }

    }
}
