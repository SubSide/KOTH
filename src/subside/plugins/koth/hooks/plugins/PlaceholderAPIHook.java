package subside.plugins.koth.hooks.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.adapter.TimeObject;
import subside.plugins.koth.scheduler.ScheduleHandler;

/**
* Made in colaboration with F64_Rx <3
*/
public class PlaceholderAPIHook extends EZPlaceholderHook {

    public PlaceholderAPIHook(Plugin plugin) {
        super(plugin, "koth");
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("nextevent")) {
            return TimeObject.getTimeTillNextEvent();
        }
        
        for(Koth koth : KothHandler.getInstance().getAvailableKoths()){
            String name = koth.getName().toLowerCase();
            if(identifier.equals("live_"+name+"_isrunning")) return koth.isRunning()?"True":"False";
            if(identifier.equals("live_"+name+"_name")) return koth.getName();
            if(identifier.equals("live_"+name+"_loot")) return koth.getLoot();
            if(identifier.equals("live_"+name+"_x")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("live_"+name+"_y")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("live_"+name+"_z")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("live_"+name+"_world")) return ""+koth.getMiddle().getWorld().getName();
            
            if(identifier.equals("live_"+name+"_nextevent")) return TimeObject.getTimeTillNextEvent(koth);
                    
            if(identifier.equals("live_"+name+"_lastwinner")){
                if(koth.getLastWinner() == null) return "";
                return koth.getLastWinner().getName();
            }
        }
        
        if(ScheduleHandler.getInstance().getNextEvent() != null){
            String sKoth = ScheduleHandler.getInstance().getNextEvent().getKoth();
            if(sKoth == null) return "";
            
            Koth koth = KothHandler.getInstance().getKoth(sKoth);
            
            if(identifier.equals("next_isrunning")) return koth.isRunning()?"True":"False";
            if(identifier.equals("next_name")) return koth.getName();
            if(identifier.equals("next_loot")) return koth.getLoot();
            if(identifier.equals("next_x")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("next_y")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("next_z")) return ""+koth.getMiddle().getBlockX();
            if(identifier.equals("next_world")) return ""+koth.getMiddle().getWorld().getName();
            
            if(identifier.equals("next_nextevent")) return TimeObject.getTimeTillNextEvent(koth);
                    
            if(identifier.equals("next_lastwinner")){
                if(koth.getLastWinner() == null) return "";
                return koth.getLastWinner().getName();
            }
        }
        
        
        if (KothHandler.getInstance().getRunningKoth() == null) return "";
        
        RunningKoth rKoth = KothHandler.getInstance().getRunningKoth();
        if (rKoth == null) return "";
        if (identifier.equals("name")) return rKoth.getKoth().getName();
        if (identifier.equals("x")) return rKoth.getKoth().getMiddle().getBlockX() + "";
        if (identifier.equals("y")) return rKoth.getKoth().getMiddle().getBlockY() + "";
        if (identifier.equals("z")) return rKoth.getKoth().getMiddle().getBlockZ() + "";
        if (identifier.equals("world")) return rKoth.getKoth().getMiddle().getWorld().getName();
        
        if (identifier.equals("player_inarea")) return rKoth.getKoth().isInArea(player)?"True":"False";
        
        if (identifier.equals("time_secondsleft")) return "" + rKoth.getTimeObject().getSecondsLeft();
        if (identifier.equals("time_minutesleft")) return "" + rKoth.getTimeObject().getMinutesLeft();
        if (identifier.equals("time_secondscapped")) return "" + rKoth.getTimeObject().getSecondsCapped();
        if (identifier.equals("time_minutescapped")) return "" + rKoth.getTimeObject().getMinutesCapped();
        if (identifier.equals("time_leftformatted")) return "" + rKoth.getTimeObject().getTimeLeftFormatted();
        if (identifier.equals("time_cappedformatted")) return "" + rKoth.getTimeObject().getTimeCappedFormatted();
        if (identifier.equals("time_totalsecondsleft")) return "" + rKoth.getTimeObject().getTotalSecondsLeft();
        if (identifier.equals("time_totalsecondscapped")) return "" + rKoth.getTimeObject().getTotalSecondsCapped();
        if (identifier.equals("time_lengthinminutes")) return "" + rKoth.getTimeObject().getLengthInMinutes();
        if (identifier.equals("time_lengthinseconds")) return "" + rKoth.getTimeObject().getLengthInSeconds();
        
        if (identifier.equals("currentcapper")) {
            if (rKoth.getCapper() == null) return "No One";
            return rKoth.getCapper().getName();
        }
        
        
        if (identifier.equals("loot_name")) {
            if (rKoth.getLootChest() == null) return "No Loot Set";
            return rKoth.getLootChest();
        }
        if (identifier.equals("loot_x")) {
            if (rKoth.getKoth().getLootPos() == null) return "No Loot Set";
            return "" + rKoth.getKoth().getLootPos().getBlockX();
        }
        if (identifier.equals("loot_y")) {
            if (rKoth.getKoth().getLootPos() == null) return "No Loot Set";
            return "" + rKoth.getKoth().getLootPos().getBlockY();
        }
        if (identifier.equals("loot_z")) {
            if (rKoth.getKoth().getLootPos() == null) return "No Loot Set";
            return "" + rKoth.getKoth().getLootPos().getBlockZ();
        }
        if (identifier.equals("loot_world")) {
            if (rKoth.getKoth().getLootPos() == null) return "No Loot Set";
            return "" + rKoth.getKoth().getLootPos().getWorld().getName();
        }
            
        
        return "";
    }

}
