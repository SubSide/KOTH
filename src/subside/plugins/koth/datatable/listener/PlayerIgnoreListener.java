package subside.plugins.koth.datatable.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class PlayerIgnoreListener implements Listener {

    private DataTable dataTable;

    public PlayerIgnoreListener(DataTable dataTable){
        this.dataTable = dataTable;
    }

    /**
     * Used to save the player's ignore state
     */
    @EventHandler(ignoreCancelled=true, priority= EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event){
        // Cancel immediately if the player isn't ignoring.
        if(!Utils.isIgnoring(event.getPlayer()))
            return;

        try {
            Connection connection = dataTable.getDatabaseProvider().getConnection();
            connection.setAutoCommit(false); // Start transaction

            PreparedStatement ptsd = connection.prepareStatement(
                    "INSERT INTO player_ignore(id, player_uuid) "
                            + "VALUES (NULL, ?)");
            ptsd.setString(1, event.getPlayer().getUniqueId().toString());
            ptsd.execute();

            connection.commit(); // Commit the transaction
            connection.setAutoCommit(true);

        }
        catch (SQLException e) {
            dataTable.getPlugin().getLogger().log(Level.SEVERE, "Couldn't execute a query!", e);
        }
    }

    /**
     * Used to load the player's ignore state
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event){
        try {
            Connection connection = dataTable.getDatabaseProvider().getConnection();
            connection.setAutoCommit(false); // Start transaction

            PreparedStatement ptsd = connection.prepareStatement("SELECT * FROM player_ignore WHERE player_uuid=?");
            ptsd.setString(1, event.getPlayer().getUniqueId().toString());
            ResultSet result = ptsd.executeQuery();

            if(result.next()){
                if(!Utils.isIgnoring(event.getPlayer()))
                    Utils.toggleIgnoring(dataTable.getPlugin(), event.getPlayer());
            }

            ptsd = connection.prepareStatement("DELETE FROM player_ignore WHERE player_uuid=?");
            ptsd.setString(1, event.getPlayer().getUniqueId().toString());
            ptsd.execute();

            connection.commit(); // Commit the transaction
            connection.setAutoCommit(true);
        }
        catch (SQLException e) {
            dataTable.getPlugin().getLogger().log(Level.SEVERE, "Couldn't execute a query!", e);
        }
    }
}
