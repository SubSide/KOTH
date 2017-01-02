package subside.plugins.koth.gamemodes;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.exceptions.KothNotExistException;
import subside.plugins.koth.modules.KothHandler;

public class StartParams {
        private @Getter @Setter Koth koth;
        private @Getter @Setter String gamemode = "classic";
        private @Getter @Setter int captureTime = 15*60;
        private @Getter @Setter int maxRunTime = -1;
        private @Getter @Setter int lootAmount = 5;
        private @Setter String lootChest = null;
        private @Getter @Setter boolean isScheduled = false;
        private @Getter @Setter String entityType = null;
        
        public String getLootChest(){
            if(lootChest != null) return null;
            return koth.getLoot();
        }
        
        public StartParams(KothHandler kothHandler, String kth) throws KothNotExistException{
            KothPlugin plugin = kothHandler.getPlugin();
            
            this.lootAmount = plugin.getConfigHandler().getLoot().getLootAmount();
            
        	gamemode = plugin.getGamemodeRegistry().getCurrentMode();
            if (kth.equalsIgnoreCase("$random")) {
                if (plugin.getKothHandler().getAvailableKoths().size() > 0) {
                    kth = plugin.getKothHandler().getAvailableKoths().get(new Random().nextInt(plugin.getKothHandler().getAvailableKoths().size())).getName();
                }
            } else if(kth.equalsIgnoreCase("$rotation")){
                kth = plugin.getScheduleHandler().getMapRotation().getNext();
            }

            for (Koth koth : plugin.getKothHandler().getAvailableKoths()) {
                if (koth.getName().equalsIgnoreCase(kth)) {
                    this.koth = koth;
                    return;
                }
            }
            throw new KothNotExistException(plugin.getKothHandler(), kth);
        }
    }