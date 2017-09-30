package subside.plugins.koth.modules;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.utils.Perm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionChecker extends AbstractModule implements Listener {

    private @Getter String newVersion = null;

    public VersionChecker(KothPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        // Register the events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), this::checkUpdate);
    }

    @Override
    public void onDisable() {
        // Remove all previous event handlers
        HandlerList.unregisterAll(this);
    }

    private void checkUpdate() {
        try {
            // Open a connection to our version checker URL
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=7689");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/1.0");
            conn.setConnectTimeout(1000);
            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine = in.readLine();
            in.close();

            // If our version is the same as the latest version online, we ignore this
            if(getPlugin().getDescription().getVersion().equals(inputLine)) {
                return;
            }

            // Otherwise we update newVersion to what we read online
            newVersion = inputLine;

        } catch (Exception e) {
            this.getPlugin().getLogger().info("Couldn't check for updates");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        // We also want the message to pop up when a player that has the admin.admin perm joins.
        if(newVersion != null && Perm.Admin.ADMIN.has(event.getPlayer())){
            String[] msgs = {
                    ChatColor.translateAlternateColorCodes('&', "&aAn update for &2KoTH (KoTH "+newVersion+") &ais available at:"),
                    ChatColor.translateAlternateColorCodes('&', "&ahttps://www.spigotmc.org/resources/KoTH.7689/")
            };
            event.getPlayer().sendMessage(msgs);
        }
    }
}