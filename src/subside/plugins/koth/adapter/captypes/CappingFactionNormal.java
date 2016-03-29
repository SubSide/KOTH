package subside.plugins.koth.adapter.captypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.exceptions.NoCompatibleCapperException;

public class CappingFactionNormal extends CappingFaction {
private com.massivecraft.factions.entity.Faction faction;
    
    public CappingFactionNormal(com.massivecraft.factions.entity.Faction faction){
        this.faction = faction;
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return com.massivecraft.factions.entity.MPlayerColl.get().get(oPlayer).getFactionId().equals(faction.getId());
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "faction";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return faction.getId();
    }
    
    @Override
    public String getName(){
        return faction.getName();
    }
    
    public CappingFactionNormal(List<Player> playerList2){
        List<Player> playerList = new ArrayList<Player>(playerList2);
        Collections.shuffle(playerList);
        for(Player player : playerList){
            com.massivecraft.factions.entity.Faction fac = com.massivecraft.factions.entity.MPlayerColl.get().get(player).getFaction();
            if(fac.isNormal()){
                faction = fac;
                break;
            }
        }
        if(faction == null){
        	throw new NoCompatibleCapperException();
        }
    }
    
    public com.massivecraft.factions.entity.Faction getObject(){
        return faction;
    }

    @Override
    public boolean areaCheck(Capable cap) {
        for(Player player : faction.getOnlinePlayers()){
            if(cap.isInArea(player)){
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
        return new CappingFactionNormal(com.massivecraft.factions.entity.FactionColl.get().get(name));
    }
}
