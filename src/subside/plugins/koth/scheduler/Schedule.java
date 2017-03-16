package subside.plugins.koth.scheduler;

import java.util.logging.Level;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;
import subside.plugins.koth.exceptions.KothException;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.JSONSerializable;
import subside.plugins.koth.utils.MessageBuilder;

public class Schedule implements JSONSerializable<Schedule> {
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
    
    private @Getter ScheduleHandler scheduleHandler;

    public Schedule(ScheduleHandler scheduleHandler){
        this.scheduleHandler = scheduleHandler;
    }
    
    public Schedule(ScheduleHandler scheduleHandler, String koth, Day day, String time) {
        this.scheduleHandler = scheduleHandler;
        this.koth = koth;
        this.day = day;
        this.time = time;
        calculateNextEvent();

    }

    public void calculateNextEvent() {
        long eventTime = day.getDayStart(scheduleHandler.getPlugin()) + Day.getTime(time) - WEEK;
        
        eventTime += scheduleHandler.getPlugin().getConfigHandler().getGlobal().getScheduleMinuteOffset()*60*1000;

        while (eventTime < System.currentTimeMillis()) {
            eventTime += WEEK;
        }
        nextEventMillis = eventTime;
        
        if(scheduleHandler.getPlugin().getConfigHandler().getGlobal().isDebug()){
            scheduleHandler.getPlugin().getLogger().log(Level.WARNING, "Schedule created for: "+day+" "+time+" "+nextEventMillis);
        }
    }

    public void tick() {
        if (scheduleHandler.getPlugin().getConfigHandler().getGlobal().getPreBroadcast() != 0) {
            if(!isBroadcasted){
                if (System.currentTimeMillis() + 1000 * 60 * scheduleHandler.getPlugin().getConfigHandler().getGlobal().getPreBroadcast() > nextEventMillis) {
                    isBroadcasted = true;
                    new MessageBuilder(Lang.KOTH_PLAYING_PRE_BROADCAST).maxTime(maxRunTime).captureTime(captureTime).lootAmount(lootAmount).koth(scheduleHandler.getPlugin().getKothHandler(), koth).buildAndBroadcast();
                }
            }
        }

        if (System.currentTimeMillis() > nextEventMillis) {
            setNextEventTime();
            isBroadcasted = false;
            try {
                scheduleHandler.getPlugin().getKothHandler().startKoth(this);
            } catch(KothException e){
                scheduleHandler.getPlugin().getLogger().log(Level.WARNING, "Koth is already running");
            }
        }
    }

    private void setNextEventTime() {
        nextEventMillis += WEEK;
    }

    public long getNextEvent() {
        return nextEventMillis;
    }

    public Schedule load(JSONObject obj) {
        this.koth =  (String) obj.get("koth"); // koth
        this.day = Day.getDay((String)obj.get("day")); // day
        this.time = (String) obj.get("time"); // time
        
        
        if (obj.containsKey("captureTime")) {
            this.setCaptureTime(Integer.parseInt(obj.get("captureTime")+"")); // runTime
        }

        if (obj.containsKey("maxRunTime")) {
            this.setMaxRunTime(Integer.parseInt(obj.get("maxRunTime")+"")); // maxRunTime
        }

        if (obj.containsKey("lootAmount")) {
            this.setLootAmount(Integer.parseInt(obj.get("lootAmount")+"")); // lootAmount
        }

        if (obj.containsKey("lootChest")) {
            this.setLootChest((String) obj.get("lootChest")); // lootChest
        }
        
        if(obj.containsKey("entityType")){
            this.setEntityType((String) obj.get("entityType"));
        }

        calculateNextEvent();
        return this;

    }

    @SuppressWarnings("unchecked")
    public JSONObject save() {
        JSONObject obj = new JSONObject();
        obj.put("koth", this.koth); // koth
        obj.put("day", this.day.getDay()); // day
        obj.put("time", this.time); // time

        if (captureTime != -1) {
            obj.put("captureTime", this.captureTime); // runTime
        }

        if (maxRunTime != -1) {
            obj.put("maxRunTime", this.maxRunTime); // maxRunTime
        }

        if (lootAmount != -1 || lootAmount == scheduleHandler.getPlugin().getConfigHandler().getLoot().getLootAmount()) {
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
