package subside.plugins.koth.datatable.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import subside.plugins.koth.datatable.DataTable;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KothWinListener implements Listener {
    DataTable dataTable;
    public KothWinListener(DataTable dataTable){
        this.dataTable = dataTable;
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onKothEnd(KothEndEvent event){

        if(event.getReason() != EndReason.GRACEFUL && event.getReason() != EndReason.WON){
            return;
        }
        
        if(event.getWinner() == null || event.getWinner().getObject() == null){
            return;
        }


        try {
            Connection connection = dataTable.getDatabaseProvider().getConnection();
            
            connection.setAutoCommit(false); // Start transaction
            
            PreparedStatement ptsd = connection.prepareStatement(
                    "INSERT INTO results(id, koth, gamemode, date, capper_uuid, capper_displayname, capper_type) "
                    + "VALUES (NULL, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ptsd.setString(1, event.getKoth().getName());
            ptsd.setString(2, event.getRunningKoth().getType());
            ptsd.setInt(3, (int)(System.currentTimeMillis()/1000));
            ptsd.setString(4, event.getWinner().getUniqueObjectIdentifier());
            ptsd.setString(5, event.getWinner().getName());
            ptsd.setString(6, event.getWinner().getUniqueClassIdentifier());
            ptsd.execute();
            
            ResultSet keys = ptsd.getGeneratedKeys();
            if(!keys.next()){
                connection.rollback();
                throw new SQLException("Something went wrong! Couldn't get the insterted ID!");
            }
            
            long resultId = keys.getLong(1);
            
            ptsd = connection.prepareStatement(
                    "INSERT INTO player_results(id, result_id, player_uuid, player_displayname) "
                    + "VALUES (NULL, ?, ?, ?)");

            ptsd.setLong(1, resultId);
            
            List<Player> players = new ArrayList<>(event.getWinner().getAvailablePlayers(event.getKoth()));
            for(Player player : players){
                ptsd.setString(2, player.getUniqueId().toString());
                ptsd.setString(3, player.getName());
                ptsd.execute();
            }
            
            connection.commit(); // Commit the transaction
            connection.setAutoCommit(true);
            
        }
        catch (SQLException e) {
            dataTable.getPlugin().getLogger().log(Level.SEVERE, "Couldn't execute a query!", e);
        }
    }
}
