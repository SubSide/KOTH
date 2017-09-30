package subside.plugins.koth.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import subside.plugins.koth.exceptions.KothException;
import subside.plugins.koth.modules.ConfigHandler;
import subside.plugins.koth.utils.JSONSerializable;
import subside.plugins.koth.utils.MessageBuilder;

import java.util.List;
import java.util.logging.Level;

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

    private int preBroadcastIndex = 0;
    
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

    /**
     * Calculate when the next event should happen
     */
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

        // Broadcast stuff
        List<Integer> times = scheduleHandler.getPlugin().getConfigHandler().getGlobal().getPreBroadcastTimes();
        this.preBroadcastIndex = times.size()-1;
        while(preBroadcastIndex >= 0 && (nextEventMillis - times.get(preBroadcastIndex) * 1000 < System.currentTimeMillis())){
            preBroadcastIndex--;
        }
    }

    /**
     * This method is executed every second,
     * it checks if it should broadcast anything and if it should run
     */
    public void tick() {
        // Broadcast stuff
        ConfigHandler.Global cGlobal = scheduleHandler.getPlugin().getConfigHandler().getGlobal();
        if (cGlobal.isPreBroadcast() && preBroadcastIndex >= 0) {
            int time = cGlobal.getPreBroadcastTimes().get(preBroadcastIndex);
            long nextBroadcast = nextEventMillis - time * 1000;
            if (System.currentTimeMillis() > nextBroadcast) {
                new MessageBuilder(cGlobal.getPreBroadcastMessages().get(time)).maxTime(maxRunTime).captureTime(captureTime).lootAmount(lootAmount).koth(scheduleHandler.getPlugin().getKothHandler(), koth).buildAndBroadcast();
                preBroadcastIndex--;
            }
        }

        // Code for when a KoTH should start
        if (System.currentTimeMillis() > nextEventMillis) {
            // Reset prebroadcasting
            preBroadcastIndex = scheduleHandler.getPlugin().getConfigHandler().getGlobal().getPreBroadcastTimes().size()-1;

            // Set the next event time
            setNextEventTime();
            try {
                scheduleHandler.getPlugin().getKothHandler().startKoth(this);
            } catch(KothException e){
                scheduleHandler.getPlugin().getLogger().warning("Koth is already running");
            }
        }
    }

    private void setNextEventTime() {
        nextEventMillis += WEEK;
    }

    /**
     * Simple getter for nextEventMillis
     * @return When the next event should run (in milliseconds)
     */
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
