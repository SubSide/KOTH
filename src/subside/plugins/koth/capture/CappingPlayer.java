package subside.plugins.koth.capture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.areas.Capable;
import subside.plugins.koth.hooks.HookManager;

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
        return ConfigHandler.getInstance().getGlobal().isUseFancyPlayerName()?player.getPlayer().getDisplayName():player.getName();
    }
    
    public CappingPlayer(List<Player> playerList){
        player = playerList.get(new Random().nextInt(playerList.size()));
    }
    
    public OfflinePlayer getObject(){
        return player;
    }
    
    @Override
    public boolean areaCheck(Capable cap){
        if(cap.isInArea(player) && HookManager.getHookManager().canCap(player.getPlayer())){
            return true;
        }
        
        return false;
    }
    
    @Override
    public List<Player> getAllOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        if(player.isOnline()){
            list.add(player.getPlayer());
        }
        return list;
    }
    
    public static Capper getFromUniqueName(String name){
        return new CappingPlayer(Bukkit.getOfflinePlayer(UUID.fromString(name)));
    }
}