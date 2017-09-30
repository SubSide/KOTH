package subside.plugins.koth.gamemodes;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.scheduler.Schedule;

/**
 * @author Thomas "SubSide" van den Bulk
 *
 */
public class TimeObject {
    private int captureTime;
    private int timeCapped;
    
    public TimeObject(int captureTime, int timeCapped){
        this.captureTime = captureTime;
        this.timeCapped = timeCapped;
    }
    
    public String getTimeCappedFormatted(){
        return String.format("%02d", getMinutesCapped())+":"+String.format("%02d", getSecondsCapped());
    }
    
    public String getTimeLeftFormatted(){
        return String.format("%02d", getMinutesLeft())+":"+String.format("%02d", getSecondsLeft());
    }
    
    public int getSecondsCapped(){
        return timeCapped%60;
    }
    
    public int getTotalSecondsCapped(){
        return timeCapped;
    }
    
    public int getMinutesCapped(){
        return timeCapped/60;
    }

    public int getPercentageCapped(){
        return (timeCapped*100)/(captureTime);
    }
    
    
    // Captime left
    public int getSecondsLeft(){
        return (captureTime - timeCapped) % 60;
    }
    
    public int getTotalSecondsLeft(){
        return captureTime - timeCapped;
    }
    
    public int getMinutesLeft(){
        return (captureTime - timeCapped) / 60;
    }

    public int getPercentageLeft(){
        return 100 - getPercentageCapped();
    }
    
    
    // Total Captime
    public int getLengthInMinutes(){
        return captureTime/60;
    }
    
    public int getLengthInSeconds(){
        return captureTime;
    }
    
    
    // Time till next event (static)
    public static String getTimeTillNextEvent(KothPlugin plugin){
        if(plugin.getKothHandler().getRunningKoth() != null) return "--:--:--";
        
        return getTimeTillNextEvent(plugin.getScheduleHandler().getNextEvent());
    }
    
    public static String getTimeTillNextEvent(KothPlugin plugin, Koth koth){
        if(koth.isRunning()) getTimeTillNextEvent((Schedule)null);
        
        return getTimeTillNextEvent(plugin.getScheduleHandler().getNextEvent(koth));
    }
    
    public static String getTimeTillNextEvent(Schedule schedule){
        String ret = Lang.HOOKS_PLACEHOLDERAPI_TIMETILL[0];
        if(schedule == null){
            return ret.replaceAll("%h%", "-").replaceAll("%m%", "-").replaceAll("%s%", "-").replaceAll("%hh%", "-").replaceAll("%mm%", "-").replaceAll("%ss%", "-");
        }
        
        long timeTillNext = schedule.getNextEvent() - System.currentTimeMillis();
        timeTillNext /= 1000;
        
        long secs = timeTillNext%60;
        timeTillNext /= 60;
        
        long mins = timeTillNext%60;
        timeTillNext /= 60;
        
        long hours = timeTillNext;

        ret = ret.replaceAll("%hh%", String.format("%02d", hours)).replaceAll("%mm%", String.format("%02d", mins)).replaceAll("%ss%", String.format("%02d", secs));
        ret = ret.replaceAll("%h%", ""+hours).replaceAll("%m%", ""+mins).replaceAll("%s%", ""+secs);
        return ret;
    }
}
