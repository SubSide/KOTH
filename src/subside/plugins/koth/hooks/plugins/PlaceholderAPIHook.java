package subside.plugins.koth.hooks.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.Koth;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.adapter.RunningKoth;
import subside.plugins.koth.adapter.TimeObject;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;

/**
* Made in collaboration with F64_Rx <3
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
        
        
        // Loop over all all live KoTH's
        for(Koth koth : KothHandler.getInstance().getAvailableKoths()){
            String replacer = replaceHolders(identifier, "live_"+koth.getName().toLowerCase()+"_", koth, player);
            if(replacer != null) return replacer;
        }
        
        // Loop over all schedules.
        if(ScheduleHandler.getInstance().getNextEvent() != null){
            Schedule schedule = ScheduleHandler.getInstance().getNextEvent();
            if(schedule == null) return "";
            
            if(schedule.getKoth().startsWith("$")){
                String scheduleReplacer = replaceSchedulingHolders(identifier, "next_", schedule);
                if(scheduleReplacer != null) return scheduleReplacer;
                
                return "???";
            }
            
            Koth koth = KothHandler.getInstance().getKoth(schedule.getKoth());
            if(koth == null) return "";
            
            String replacer = replaceHolders(identifier, "next_", koth, player);
            if(replacer != null) return replacer;
        }
        
        // Check if  there is a running KoTH
        if (KothHandler.getInstance().getRunningKoth() == null) return "";
        
        RunningKoth rKoth = KothHandler.getInstance().getRunningKoth();
        if (rKoth == null) return "";
        
        String replacer = replaceHolders(identifier, "", rKoth.getKoth(), player);
        if(replacer != null) return replacer;
        
        return "";
    }
    
    public String replaceHolders(String identifier, String prefix, Koth koth, Player player){
        
        if (identifier.equals(prefix+"name")) return koth.getName();
        if (identifier.equals(prefix+"x")) return koth.getMiddle().getBlockX() + "";
        if (identifier.equals(prefix+"y")) return koth.getMiddle().getBlockY() + "";
        if (identifier.equals(prefix+"z")) return koth.getMiddle().getBlockZ() + "";
        if (identifier.equals(prefix+"world")) return koth.getMiddle().getWorld().getName();
        
        if (identifier.equals(prefix+"player_inarea")) return koth.isInArea(player)?"True":"False";
        

        if(identifier.equals(prefix+"nextevent")) return TimeObject.getTimeTillNextEvent(koth);
                
        if(identifier.equals(prefix+"lastwinner")){
            if(koth.getLastWinner() == null) return "";
            return koth.getLastWinner().getName();
        }
        
        if(identifier.equals(prefix+"isrunning")) return koth.isRunning()?"True":"False";

        
        if (identifier.equals(prefix+"loot_x")) {
            if (koth.getLootPos() == null) return "No Loot Set";
            return "" + koth.getLootPos().getBlockX();
        }
        if (identifier.equals(prefix+"loot_y")) {
            if (koth.getLootPos() == null) return "No Loot Set";
            return "" + koth.getLootPos().getBlockY();
        }
        if (identifier.equals(prefix+"loot_z")) {
            if (koth.getLootPos() == null) return "No Loot Set";
            return "" + koth.getLootPos().getBlockZ();
        }
        if (identifier.equals(prefix+"loot_world")) {
            if (koth.getLootPos() == null) return "No Loot Set";
            return "" + koth.getLootPos().getWorld().getName();
        }
        
        RunningKoth rKoth = koth.getRunningKoth();
        if (rKoth != null){
            return replaceRunningKothHolders(identifier, prefix, rKoth);
        }
        
        
        return null;
    }
    
    public String replaceSchedulingHolders(String identifier, String prefix, Schedule schedule){
        if (identifier.equals(prefix+"nextevent")) return TimeObject.getTimeTillNextEvent(schedule);
        if (identifier.equals(prefix+"name")) return schedule.getKoth();
        if (identifier.equals(prefix+"time")) return schedule.getTime();
        return null;
    }
    
    public String replaceRunningKothHolders(String identifier, String prefix, RunningKoth rKoth){

        if (identifier.equals(prefix+"time_secondsleft")) return "" + rKoth.getTimeObject().getSecondsLeft();
        if (identifier.equals(prefix+"time_minutesleft")) return "" + rKoth.getTimeObject().getMinutesLeft();
        if (identifier.equals(prefix+"time_secondscapped")) return "" + rKoth.getTimeObject().getSecondsCapped();
        if (identifier.equals(prefix+"time_minutescapped")) return "" + rKoth.getTimeObject().getMinutesCapped();
        if (identifier.equals(prefix+"time_leftformatted")) return "" + rKoth.getTimeObject().getTimeLeftFormatted();
        if (identifier.equals(prefix+"time_cappedformatted")) return "" + rKoth.getTimeObject().getTimeCappedFormatted();
        if (identifier.equals(prefix+"time_totalsecondsleft")) return "" + rKoth.getTimeObject().getTotalSecondsLeft();
        if (identifier.equals(prefix+"time_totalsecondscapped")) return "" + rKoth.getTimeObject().getTotalSecondsCapped();
        if (identifier.equals(prefix+"time_lengthinminutes")) return "" + rKoth.getTimeObject().getLengthInMinutes();
        if (identifier.equals(prefix+"time_lengthinseconds")) return "" + rKoth.getTimeObject().getLengthInSeconds();
        if (identifier.equals(prefix+"time_percentagecapped")) return "" + rKoth.getTimeObject().getPercentageCapped();
        if (identifier.equals(prefix+"time_percentageleft")) return "" + rKoth.getTimeObject().getPercentageLeft();
        
        if (identifier.equals(prefix+"currentcapper")) {
            if (rKoth.getCapper() == null) return Lang.HOOKS_PLACEHOLDERAPI_NOONECAPPING[0];
            return rKoth.getCapper().getName();
        }
        
        
        if (identifier.equals(prefix+"loot_name")) {
            if (rKoth.getLootChest() == null) return "No Loot Set";
            return rKoth.getLootChest();
        }
        
        return null;
    }

}
