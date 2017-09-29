package subside.plugins.koth.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.areas.Area;
import subside.plugins.koth.areas.Capable;
import subside.plugins.koth.areas.Koth;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.gamemodes.TimeObject;
import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.scheduler.Schedule;
import subside.plugins.koth.scheduler.ScheduleHandler;

public class MessageBuilder {
    private StrObj message;
    private Collection<Player> excluders;
    private Collection<Player> includers;
    private static @Setter KothPlugin plugin;

    private class StrObj {
        private String[] message;

        private StrObj(String[] message) {
            if (message == null){
                this.message = new String[] {};
            } else {
                this.message = message.clone();
            }
        }

        protected StrObj replaceAll(String search, String replace) {
            for (int x = 0; x < message.length; x++) {
                try {
                    message[x] = message[x].replaceAll(search, replace);
                } catch(Exception e){
                    System.out.println(search+" : "+replace);
                    e.printStackTrace();
                }
            }
            return this;
        }

        protected String[] build() {
            // A way to allow %ttn% to be used everywhere
            if(plugin != null){
                this.replaceAll("%ttn%", TimeObject.getTimeTillNextEvent(plugin));
            }

            for (int x = 0; x < message.length; x++) {
                message[x] = ChatColor.translateAlternateColorCodes('&', message[x]);
            }
            return message;
        }

    }

    public MessageBuilder(String[] msg) {
        this.message = new StrObj(msg);
    }
    
    public MessageBuilder(String msg){
        this.message = new StrObj(new String[]{msg});
    }

    public MessageBuilder koth(KothHandler kothHandler, String koth) {
        // Make sure that $ placeholders aren't causing issues
        koth = koth.replaceAll("\\$", "\\\\\\$");
        
        Koth kth = kothHandler.getKoth(koth);
        if (kth != null) {
            koth(kth);
        } else {
            message.replaceAll("%koth%",
                    koth.replaceAll("\\\\\\\\", "%5C")
                            .replaceAll("([^\\\\])_", "$1 ")
                            .replaceAll("\\\\_", "_")
                            .replaceAll("%5C", "\\\\\\\\"));
        }
        return this;
    }

    public MessageBuilder location(Location location){
        if (location != null) {
            message.replaceAll("%x%", "" + location.getBlockX())
                    .replaceAll("%y%", "" + location.getBlockY())
                    .replaceAll("%z%", "" + location.getBlockZ());
            try {
                message.replaceAll("%world%", location.getWorld().getName());

                if(plugin.getConfigHandler().getGlobal().isWorldFilter()){
                    this.include(location.getWorld().getPlayers());
                }

            } catch (Exception e) { e.printStackTrace(); }
        }
        return this;
    }
    
    public MessageBuilder koth(Koth koth){
        message.replaceAll("%koth%", koth.getName()
                .replaceAll("\\\\\\\\", "%5C")
                .replaceAll("([^\\\\])_", "$1 ")
                .replaceAll("\\\\_", "_")
                .replaceAll("%5C", "\\\\\\\\"));
        this.location(koth.getMiddle());
        return this;
    }
    
    public MessageBuilder area(Area area){
        message.replaceAll("%area%", area.getName());
        this.location(area.getMiddle());
        return this;
    }

    public MessageBuilder area(String area){
        message.replaceAll("%area%", area);
        return this;
    }
    
    public MessageBuilder loot(String loot){
        message.replaceAll("%loot%", loot);
        return this;
    }
    
    public MessageBuilder entry(String entry){
        message.replaceAll("%entry%", entry);
        return this;
    }
    
    public MessageBuilder title(String title){
        message.replaceAll("%title%", title);
        return this;
    }

    public MessageBuilder capper(String capper) {
        message.replaceAll("%capper%", (capper != null) ? capper: "None");
        return this;
    }

    public MessageBuilder day(String day) {
        message.replaceAll("%day%", day);
        return this;
    }

    public MessageBuilder lootAmount(int lootamount) {
        message.replaceAll("%lootamount%", lootamount + "");
        return this;
    }

    public MessageBuilder time(String time) {
        message.replaceAll("%time%", time);
        return this;
    }

    public MessageBuilder times(String times){
        message.replaceAll("%times%", times);
        return this;
    }

    public MessageBuilder captureTime(int captureTime) {
        message.replaceAll("%ct%", "" + captureTime);
        return this;
    }
    
    public MessageBuilder timeTillNext(Schedule schedule){
        message.replaceAll("%ttn%", TimeObject.getTimeTillNextEvent(schedule));
        return this;
    }

    public MessageBuilder id(int id) {
        message.replaceAll("%id%", "" + id);
        return this;
    }

    public MessageBuilder date(String date) {
        message.replaceAll("%date%", date);
        return this;
    }

    public MessageBuilder time(TimeObject tO) {
        message.replaceAll("%m%", String.format("%02d", tO.getMinutesCapped()));
        message.replaceAll("%s%", String.format("%02d", tO.getSecondsCapped()));
        message.replaceAll("%ml%", String.format("%02d", tO.getMinutesLeft()));
        message.replaceAll("%sl%", String.format("%02d", tO.getSecondsLeft()));

        return this;
    }

    public MessageBuilder maxTime(int maxTime) {
        message.replaceAll("%mt%", "" + ((int) maxTime / 60));
        return this;
    }

    public MessageBuilder command(String command) {
        message.replaceAll("%command%", command);
        return this;
    }

    public MessageBuilder commandInfo(String commandInfo) {
        message.replaceAll("%command_info%", commandInfo);
        return this;
    }

    public MessageBuilder exclude(Collection<Player> excluders){
        if(this.excluders == null)
            this.excluders = new ArrayList<>();

        this.excluders.addAll(excluders);
        return this;
    }

    public MessageBuilder include(Collection<Player> includers){
        if(this.includers == null)
            this.includers = new ArrayList<>();

        this.includers.addAll(includers);
        return this;
    }

    public MessageBuilder exclude(Capper<?> capper, Capable area){
        if(capper != null)
            this.exclude(capper.getAvailablePlayers(area));

        return this;
    }

    public void buildAndBroadcast() {
        Collection<? extends Player> players = (includers == null) ? new ArrayList<>(Bukkit.getOnlinePlayers()) : includers;
        
        if(excluders != null){
            players.removeAll(excluders);
        }
        buildAndSend(players);
    }
    
    public void buildAndSend(Capper<?> capper, Capable area){
        if(capper != null)
            buildAndSend(capper.getAvailablePlayers(area));
    }
    
    public void buildAndSend(Collection<? extends Player> players){
        String[] msg = build();
        for(Player player : players){
            for (int x = 0; x < msg.length; x++) {
                if(!msg[x].equalsIgnoreCase("")){
                    Utils.sendMsg(player, msg[x]);
                }
            }
        }
    }
    

    public void buildAndSend(CommandSender player) {
        Utils.sendMsg(player, (Object[])build());
    }

    public String[] build() {
        return message.build();
    }
    
    public List<String> buildArray(){
        return Arrays.asList(message.build());
    }
}
