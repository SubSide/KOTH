package subside.plugins.koth.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.gamemodes.TimeObject;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.scheduler.Schedule;

/**
* Made in collaboration with F64_Rx <3
*/
public class PlaceholderAPIHook extends PlaceholderExpansion {
    KothPlugin plugin;
    
    public PlaceholderAPIHook(KothPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "koth";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }


        // Loop over all all live KoTH's
        if(identifier.startsWith("live_")){
            for(Koth koth : plugin.getKothHandler().getAvailableKoths()){
                String replacer = replaceHolders(identifier, "live_"+koth.getName().toLowerCase()+"_", koth, player);
                if(replacer != null) return replacer;
            }
        }

        // Loop over all schedules.
        if(identifier.startsWith("next_")){
            if(plugin.getScheduleHandler().getNextEvent() != null){
                Schedule schedule = plugin.getScheduleHandler().getNextEvent();
                if(schedule == null) return "";

                if(schedule.getKoth().startsWith("$")){
                    String scheduleReplacer = replaceSchedulingHolders(identifier, "next_", schedule);
                    if(scheduleReplacer != null) return scheduleReplacer;

                    return "???";
                }

                Koth koth = plugin.getKothHandler().getKoth(schedule.getKoth());
                if(koth == null) return "";

                String replacer = replaceHolders(identifier, "next_", koth, player);
                if(replacer != null) return replacer;
            }
        }

        // Check if  there is a running KoTH
        if (plugin.getKothHandler().getRunningKoth() == null) return "";

        RunningKoth rKoth = plugin.getKothHandler().getRunningKoth();
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


        if(identifier.equals(prefix+"nextevent")) return TimeObject.getTimeTillNextEvent(plugin, koth);

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
