package subside.plugins.koth.scheduler;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.utils.Utils;

public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    private long startOfWeek = 0;

    public String getDay() {
        switch (this) {
            case MONDAY:
                return "Monday";
            case TUESDAY:
                return "Tuesday";
            case WEDNESDAY:
                return "Wednesday";
            case THURSDAY:
                return "Thursday";
            case FRIDAY:
                return "Friday";
            case SATURDAY:
                return "Saturday";
            case SUNDAY:
                return "Sunday";
        }
        return null;
    }
    
    public static Day getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK); 

        switch (day) {
            case Calendar.MONDAY:
                return Day.MONDAY;
            case Calendar.TUESDAY:
                return Day.TUESDAY;
            case Calendar.WEDNESDAY:
                return Day.WEDNESDAY;
            case Calendar.THURSDAY:
                return Day.THURSDAY;
            case Calendar.FRIDAY:
                return Day.FRIDAY;
            case Calendar.SATURDAY:
                return Day.SATURDAY;
            case Calendar.SUNDAY:
                return Day.SUNDAY;
        }
        return null;
    }
    
    private long getStartOfWeek(KothPlugin plugin){
        if(startOfWeek <= 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone(plugin.getConfigHandler().getGlobal().getTimeZone()));
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
    
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.MINUTE, plugin.getConfigHandler().getGlobal().getStartWeekMinuteOffset());
    
            startOfWeek = calendar.getTimeInMillis();
            if(plugin.getConfigHandler().getGlobal().isDebug()){
                plugin.getLogger().log(Level.INFO, "Schedule start of week has been set to: "+Utils.parseDate(startOfWeek, plugin)+" ("+startOfWeek+")");
            }
        }
        return startOfWeek;
    }

    public static Day getDay(String str) {
        if (str.equalsIgnoreCase("monday")) {
            return MONDAY;
        } else if (str.equalsIgnoreCase("tuesday")) {
            return TUESDAY;
        } else if (str.equalsIgnoreCase("wednesday")) {
            return WEDNESDAY;
        } else if (str.equalsIgnoreCase("thursday")) {
            return THURSDAY;
        } else if (str.equalsIgnoreCase("friday")) {
            return FRIDAY;
        } else if (str.equalsIgnoreCase("saturday")) {
            return SATURDAY;
        } else if (str.equalsIgnoreCase("sunday")) {
            return SUNDAY;
        }
        return null;
    }

    public long getDayStart(KothPlugin plugin) {
        return getStartOfWeek(plugin) + (this.ordinal() * 24 * 60 * 60 * 1000);
    }
    
    


    public static long getTime(String time) {
        int hours = 0;
        int minutes = 0;
        if (time.contains(":")) {
            String[] timz = time.split(":");
            try {
                hours = Integer.parseInt(timz[0]);
                minutes = Integer.parseInt(timz[1].replaceAll("[a-zA-Z]", ""));
            }
            catch (Exception e) {}
        } else {
            try {
                hours = Integer.parseInt(time.replaceAll("[a-zA-Z]", ""));
            }
            catch (Exception e) {}
        }

        if (time.toLowerCase().endsWith("pm") && hours % 12 != 0) {
            hours += 12;
        }
        return hours * 60 * 60 * 1000 + minutes * 60 * 1000;
    }
    
}