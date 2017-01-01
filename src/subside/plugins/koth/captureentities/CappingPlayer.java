package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import subside.plugins.koth.areas.Capable;

public class CappingPlayer extends Capper {
    private OfflinePlayer player;
    
    public CappingPlayer(CaptureTypeRegistry captureTypeRegistry, OfflinePlayer player){
        super(captureTypeRegistry);
        this.player = player;
    }
    
    public CappingPlayer(CaptureTypeRegistry captureTypeRegistry, List<Player> playerList){
        super(captureTypeRegistry);
        player = playerList.get(new Random().nextInt(playerList.size()));
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
        return captureTypeRegistry.getPlugin().getConfigHandler().getGlobal().isUseFancyPlayerName()?player.getPlayer().getDisplayName():player.getName();
    }
    
    public OfflinePlayer getObject(){
        return player;
    }
    
    @Override
    public boolean areaCheck(Capable cap){
        if(cap.isInArea(player) && captureTypeRegistry.getPlugin().getHookManager().canCap(player.getPlayer())){
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
    
    public static Capper getFromUniqueName(CaptureTypeRegistry captureTypeRegistry, String name){
        return new CappingPlayer(captureTypeRegistry, Bukkit.getOfflinePlayer(UUID.fromString(name)));
    }
}