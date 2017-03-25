package subside.plugins.koth.captureentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.brcdev.gangs.GangsPlugin;
import net.brcdev.gangs.gang.Gang;
import net.brcdev.gangs.gang.GangManager;
import subside.plugins.koth.areas.Capable;

public class CappingGang extends CappingGroup {
    private Gang gang;
    
    public CappingGang(CaptureTypeRegistry captureTypeRegistry, Gang gang){
        super(captureTypeRegistry);
        this.gang = gang;
    }
    
    public CappingGang(CaptureTypeRegistry captureTypeRegistry, List<Player> playerList2){
        super(captureTypeRegistry);
        List<Player> playerList = new ArrayList<>(playerList2);
        Collections.shuffle(playerList);
        
        GangManager gM = GangsPlugin.getInstance().gangManager;
        
        for(Player player : playerList){
            if(gM.isInGang(player)){
                this.gang = gM.getPlayersGang(player);
                break;
            }
        }
    }
    
    @Override
    public boolean isInOrEqualTo(OfflinePlayer oPlayer){
        if(!oPlayer.isOnline()) return false;
        return gang.equals(GangsPlugin.getInstance().gangManager.getPlayersGang(oPlayer));
    }

    @Override
    public String getUniqueClassIdentifier(){
        return "gang";
    }
    
    @Override
    public String getUniqueObjectIdentifier(){
        return ""+gang.getId();
    }
    
    @Override
    public String getName(){
        return gang.getName();
    }
    
    public Gang getObject(){
        return gang;
    }

    @Override
    public boolean areaCheck(Capable cap) {
        for(Player player : gang.getOnlineMembers()){
            if(cap.isInArea(player) && captureTypeRegistry.getPlugin().getHookManager().canCap(player)){
                return true;
            }
        }
        return false;
    }
    
    @Override 
    public List<Player> getAllOnlinePlayers(){
        return new ArrayList<>(gang.getOnlineMembers());
    }

    public static Capper getFromUniqueName(CaptureTypeRegistry captureTypeRegistry, String name){
        return new CappingGang(captureTypeRegistry, GangsPlugin.getInstance().gangManager.getGang(Integer.parseInt(name)));
    }
}
