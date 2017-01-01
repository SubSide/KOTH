package subside.plugins.koth.capture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import subside.plugins.koth.areas.Capable;
import subside.plugins.koth.hooks.HookManager;

public class CappingFactionUUID extends CappingGroup {
    private Faction faction;
    
    public CappingFactionUUID(Faction faction){
        this.faction = faction;
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "factionuuid";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return faction.getId();
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return FPlayers.getInstance().getByOfflinePlayer(oPlayer).getFactionId().equals(faction.getId());
    }
    
    @Override
    public String getName(){
        return faction.getTag();
    }
    
    public CappingFactionUUID(List<Player> playerList2){
        List<Player> playerList = new ArrayList<Player>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            Faction fac = FPlayers.getInstance().getByPlayer(player).getFaction();
            if(fac.isNormal()){
                faction = fac;
                break;
            }
        }
    }

    public Faction getObject(){
        return faction;
    }
    
    @Override
    public boolean areaCheck(Capable cap) {
    	if(faction == null)
    		return false;
    	
        for(Player player : faction.getOnlinePlayers()){
            if(HookManager.getHookManager().canCap(player) && cap.isInArea(player)){
                return true;
            }
        }
        return false;
    }

    
    @Override 
    public List<Player> getAllOnlinePlayers(){
        return faction.getOnlinePlayers();
    }

    public static Capper getFromUniqueName(String name){
        return new CappingFactionUUID(Factions.getInstance().getFactionById(name));
    }
}