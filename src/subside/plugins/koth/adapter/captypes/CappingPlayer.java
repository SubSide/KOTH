package subside.plugins.koth.adapter.captypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.adapter.Capable;
import subside.plugins.koth.adapter.Koth;

public class CappingPlayer extends Capper {
    private OfflinePlayer player;
    
    public CappingPlayer(OfflinePlayer player){
        this.player = player;
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        return oPlayer.getUniqueId().equals(player.getUniqueId());
    }
    
    @Override
    public String getUniqueClassIdentifier(){
        return "player";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return player.getUniqueId().toString();
    }
    
    @Override
    public String getName(){
        if(!player.isOnline()) return player.getName();
        return ConfigHandler.getCfgHandler().getGlobal().isUseFancyPlayerName()?player.getPlayer().getDisplayName():player.getName();
    }
    
    public CappingPlayer(List<Player> playerList){
        player = playerList.get(new Random().nextInt(playerList.size()));
    }
    
    public OfflinePlayer getObject(){
        return player;
    }
    
    @Override
    public boolean areaCheck(Capable cap){
        if(cap.isInArea(player)){
            return true;
        }
        
        return false;
    }
    
    @Override
    public List<Player> getAvailablePlayers(Koth koth){
        List<Player> list = new ArrayList<Player>();
        if(player.isOnline()){
            list.add(player.getPlayer());
        }
        return list;
    }
    
    public static Capper getFromUniqueName(String name){
        return new CappingPlayer(Bukkit.getOfflinePlayer(UUID.fromString(name)));
    }
}