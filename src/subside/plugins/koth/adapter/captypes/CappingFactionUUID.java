package subside.plugins.koth.adapter.captypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.hooks.HookManager;

public class CappingFactionUUID extends CappingFaction {
    private com.massivecraft.factions.Faction faction;
    
    public CappingFactionUUID(com.massivecraft.factions.Faction faction){
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
        return com.massivecraft.factions.FPlayers.getInstance().getByOfflinePlayer(oPlayer).getFactionId().equals(faction.getId());
    }
    
    @Override
    public String getName(){
        return faction.getTag();
    }
    
    public CappingFactionUUID(List<Player> playerList2){
        List<Player> playerList = new ArrayList<Player>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            com.massivecraft.factions.Faction fac = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(player).getFaction();
            if(fac.isNormal()){
                faction = fac;
                break;
            }
        }
    }

    public com.massivecraft.factions.Faction getObject(){
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
    public List<Player> getAvailablePlayers(Koth koth){
        List<Player> list = new ArrayList<Player>();
        
        List<Player> players = faction.getOnlinePlayers();
        if(players.size() > 0){
            for(Player player : players){
                if(koth.isInArea(player)){
                    list.add(player);
                }
            }
            
            if(list.size() < 1){
                list.add(players.get(0));
            }
        }
        return list;
    }
    

    public static Capper getFromUniqueName(String name){
        return new CappingFactionUUID(com.massivecraft.factions.Factions.getInstance().getFactionById(name));
    }
}
