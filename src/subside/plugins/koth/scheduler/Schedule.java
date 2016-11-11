package subside.plugins.koth.scheduler;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.ConfigHandler;
import subside.plugins.koth.Lang;
import subside.plugins.koth.adapter.KothHandler;
import subside.plugins.koth.exceptions.KothAlreadyRunningException;
import subside.plugins.koth.utils.MessageBuilder;
import subside.plugins.koth.utils.Utils;

public class Schedule {
    private long nextEventMillis;
    private @Getter @Setter String koth;
    private @Getter @Setter int captureTime = 15;
    private @Getter @Setter Day day;
    private @Getter @Setter String time;
    private @Getter @Setter int maxRunTime = -1;
    private @Getter @Setter int lootAmount = -1;
    private @Getter @Setter String lootChest;
    private @Getter @Setter String entityType;

    private @Getter boolean isBroadcasted = false;
    
    private static final long WEEK = 7 * 24 * 60 * 60 * 1000;

    public Schedule(String koth, Day day, String time) {
        this.koth = koth;
        this.day = day;
        this.time = time;
        calculateNextEvent();

    }

    public void calculateNextEvent() {
        long eventTime = day.getDayStart() + Day.getTime(time) - WEEK;

        while (eventTime < System.currentTimeMillis()) {
            eventTime += WEEK;
        }
        nextEventMillis = eventTime;
        
        if(ConfigHandler.getCfgHandler().getGlobal().isDebug()){
            Utils.log("Schedule created for: "+day+" "+time+" "+nextEventMillis);
        }
    }

    @SuppressWarnings("deprecation")
    public void tick() {
        if (ConfigHandler.getCfgHandler().getGlobal().getPreBroadcast() != 0) {
            if(!isBroadcasted){
                if (System.currentTimeMillis() + 1000 * 60 * ConfigHandler.getCfgHandler().getGlobal().getPreBroadcast() > nextEventMillis) {
                    isBroadcasted = true;
                    new MessageBuilder(Lang.KOTH_PLAYING_PRE_BROADCAST).maxTime(maxRunTime).captureTime(captureTime).lootAmount(lootAmount).koth(koth).buildAndBroadcast();
                }
            }
        }

        if (System.currentTimeMillis() > nextEventMillis) {
            setNextEventTime();
            isBroadcasted = false;
            try {
                KothHandler.getInstance().startKoth(this);
            } catch(KothAlreadyRunningException e){
                Utils.log("Koth is already running");
            }
        }
    }

    private void setNextEventTime() {
        nextEventMillis += WEEK;
    }

    public long getNextEvent() {
        return nextEventMillis;
    }

    public static Schedule load(JSONObject obj, Day tDay) {
        String tKoth = (String) obj.get("koth"); // koth
        //Day tDay = Day.getDay((String) obj.get("day")); // day
        String tTime = (String) obj.get("time"); // time
        Schedule schedule = new Schedule(tKoth, tDay, tTime);
        if (obj.containsKey("captureTime")) {
            schedule.setCaptureTime(Integer.parseInt(obj.get("captureTime")+"")); // runTime
        }

        if (obj.containsKey("maxRunTime")) {
            schedule.setMaxRunTime(Integer.parseInt(obj.get("maxRunTime")+"")); // maxRunTime
        }

        if (obj.containsKey("lootAmount")) {
            schedule.setLootAmount(Integer.parseInt(obj.get("lootAmount")+"")); // lootAmount
        }

        if (obj.containsKey("lootChest")) {
            schedule.setLootChest((String) obj.get("lootChest")); // lootChest
        }
        
        if(obj.containsKey("entityType")){
            schedule.setEntityType((String) obj.get("entityType"));
        }

        return schedule;

    }

    @SuppressWarnings("unchecked")
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("koth", this.koth); // koth
        //obj.put("day", this.day.getDay()); // day
        obj.put("time", this.time); // time

        if (captureTime != -1) {
            obj.put("captureTime", this.captureTime); // runTime
        }

        if (maxRunTime != -1) {
            obj.put("maxRunTime", this.maxRunTime); // maxRunTime
        }

        if (lootAmount != -1 || lootAmount == ConfigHandler.getCfgHandler().getLoot().getLootAmount()) {
            obj.put("lootAmount", this.lootAmount); // lootAmount
        }

        if (lootChest != null) {
            obj.put("lootChest", this.lootChest); // lootChest
        }
        
        if(entityType != null){
            obj.put("entityType", this.entityType);
        }

        return obj;
    }
}
