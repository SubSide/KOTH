package subside.plugins.koth.datatable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CaptureTypeRegistry;
import subside.plugins.koth.modules.AbstractModule;
import subside.plugins.koth.modules.ConfigHandler;

public class DataTable extends AbstractModule {
    private @Getter IDatabase databaseProvider;
    private DataTableEventListener eventListener;
    
    public DataTable(KothPlugin plugin){
        super(plugin);
    }
    
    @Override
    public void onEnable(){
        try {
            ConfigHandler.Database cDB = plugin.getConfigHandler().getDatabase();
            if(cDB.getStoragetype().equalsIgnoreCase("sqlite")){
                databaseProvider = new SQLite(plugin);
                this.plugin.getLogger().log(Level.INFO, "Connected to the SQLite server!");
            } else if(cDB.getStoragetype().equalsIgnoreCase("mysql")) {
                databaseProvider = new MySQL(plugin);
                this.plugin.getLogger().log(Level.INFO, "Connected to the MySQL server!");
            } else {
                this.plugin.getLogger().log(Level.SEVERE, "The selected storagetype \"" + cDB.getStoragetype() + "\" is not available!");
                return;
            }
        } catch(Exception e){
            this.plugin.getLogger().log(Level.SEVERE, "An error occured when connecting to the database!", e);
            return;
        }
        try {
            if(databaseProvider.getConnection() == null){
                this.plugin.getLogger().log(Level.SEVERE, "Database connection could not be established!");
                return;
            }
        } catch (SQLException e) {}
        
        
        initialize();
        eventListener = new DataTableEventListener(this);
        plugin.getServer().getPluginManager().registerEvents(eventListener, plugin);
    }
    
    @Override
    public void onDisable(){
        HandlerList.unregisterAll(eventListener);
    }
    
    private void initialize(){
        try {
            // Result table
            Connection con = databaseProvider.getConnection();
            PreparedStatement ptsd = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS results ("
                    + ((databaseProvider instanceof SQLite)?"id INTEGER PRIMARY KEY":"id INT(16) NOT NULL AUTO_INCREMENT") + ", "
                    + "koth VARCHAR(32) NOT NULL, "
                    + "gamemode VARCHAR(32) NOT NULL, "
                    + "date INT(16) NOT NULL, "
                    + "capper_uuid VARCHAR(36) NOT NULL, "
                    + "capper_displayname VARCHAR(64) NOT NULL, "
                    + "capper_type VARCHAR(32) NOT NULL"
                    + ((databaseProvider instanceof SQLite)?"":", PRIMARY KEY (id)")
                    + ")");
            
            ptsd.execute();
            
            // Player based table
            ptsd = con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS player_results ("
                    + ((databaseProvider instanceof SQLite)?"id INTEGER PRIMARY KEY":"id INT(16) NOT NULL AUTO_INCREMENT") + ", "
                    + "result_id VARCHAR(32) NOT NULL, "
                    + "player_uuid VARCHAR(36) NOT NULL, "
                    + "player_displayname VARCHAR(64) NOT NULL"
                    + ((databaseProvider instanceof SQLite)?"":", PRIMARY KEY (id)")
                    + ")");
            
            ptsd.execute();
        }
        catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error: Couldn't create the database table: results", e);
        }
    }
    
    
    /** There is a very good reason that I have chosen for List<Entry<,>>
     * The reason is that yes, I could easily have used a map (hashmap for example),
     * There is only one major issue with it, and that is that you can't assume that a hashmap is ordered.
     * Using a List I can guarantee that the returned list is ordered.
     * 
     * The list returned contains entries with a Capper object, and an integer with the amount of times they won.
     * You can use capper.getName() to get the name, capper.getObject() to get the raw object
     * 
     * @param maxRows        The maximum amount of rows to be returned (Required)
     * @param fromTime       From which starting date (in unixtimestamp) it should return the results (0 to ignore)
     * @param captureType    The capturetype to filter on (e.g. player or faction) (null to ignore)
     * @param gameMode       The gamemode to filter on (e.g. classic or conquest) (null to ignore)
     * @param koth           The koth to filter on (null to ignore)
     * 
     * @return               A list with cappers and how many times they won
     **/
     public List<Entry<Capper, Integer>> getTop(int maxRows, int fromTime, String captureType, String gameMode, String koth){
         List<Entry<Capper, Integer>> top = new ArrayList<Entry<Capper, Integer>>();
         
             
         try {
             SimpleQueryBuilder sQB = new SimpleQueryBuilder("count(capper_uuid) as result, capper_uuid, capper_type", "results");
             
             if(fromTime > 0)
                 sQB.addWhere("date >= ?", fromTime);
             
             if(captureType != null)
                 sQB.addWhere("capper_type = ?", captureType);
             
             if(gameMode != null)
                 sQB.addWhere("gamemode = ?", gameMode);
             
             if(koth != null)
                 sQB.addWhere("koth = ?", koth);
             
             
             sQB.groupBy("capper_uuid");
             sQB.orderBy("result DESC");
             sQB.limit(maxRows);
             
             ResultSet result = sQB.execute();
             
             CaptureTypeRegistry cER = plugin.getCaptureTypeRegistry();
             while(result.next()){
                 top.add(new SimpleEntry<Capper, Integer>(cER.getCapperFromType(result.getString("capper_type"), result.getString("capper_uuid")), result.getInt("result")));
             }
             return top;
         } catch(SQLException e){
             plugin.getLogger().log(Level.SEVERE, "Error executing query", e);
         }
         return null;
     }
     
     /** There is a very good reason that I have chosen for List<Entry<,>>
      * The reason is that yes, I could easily have used a map (hashmap for example),
      * There is only one major issue with it, and that is that you can't assume that a hashmap is ordered.
      * Using a List I can guarantee that the returned list is ordered.
      * 
      * The list returned contains entries containing a player and an integer, 
      * the integer is the amount of times the player has won during the given timestamp
      * 
      * @param maxRows        The maximum amount of rows to be returned (Required)
      * @param fromTime       From which starting date (in unixtimestamp) it should return the results (0 to ignore)
      * @param captureType    The capturetype to filter on (e.g. player or faction) (null to ignore)
      * @param gameMode       The gamemode to filter on (e.g. classic or conquest) (null to ignore)
      * @param koth           The koth to filter on (null to ignore)
      * 
      * @return               A list with cappers and how many times they won
      **/
      public List<Entry<OfflinePlayer, Integer>> getPlayerTop(int maxRows, int fromTime, String captureType, String gameMode, String koth){
          List<Entry<OfflinePlayer, Integer>> top = new ArrayList<>();
           
          try {
              SimpleQueryBuilder sQB = new SimpleQueryBuilder("count(player_results.id) as result, player_results.player_uuid", "player_results");
              
              sQB.leftJoin("results", "player_results.result_id=results.id");
              
              if(fromTime > 0)
                  sQB.addWhere("results.date >= ?", fromTime);
              
              if(captureType != null)
                  sQB.addWhere("results.capper_type = ?", captureType);
              
              if(gameMode != null)
                  sQB.addWhere("results.gamemode = ?", gameMode);
              
              if(koth != null)
                  sQB.addWhere("results.koth = ?", koth);
              
              
              sQB.groupBy("player_results.player_uuid");
              sQB.orderBy("result DESC");
              sQB.limit(maxRows);
              
              ResultSet result = sQB.execute();
              
              while(result.next()){
                  top.add(new SimpleEntry<OfflinePlayer, Integer>(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("player_uuid"))), result.getInt("result")));
              }
              return top;
          } catch(SQLException e){
              plugin.getLogger().log(Level.SEVERE, "Error executing query", e);
          }
          return null;
      }
    
    protected class SimpleQueryBuilder {
        private String select;
        private String leftJoin;
        
        private String groupBy;
        private String orderBy;
        
        private List<String> wheres;
        private List<Object> params;
        
        private int limit = -1;
        
        protected SimpleQueryBuilder(String select, String db){
            this.select = "SELECT " + select + " FROM " + db;
            wheres = new ArrayList<>();
            params = new ArrayList<>();
        }
        
        protected SimpleQueryBuilder groupBy(String groupBy){
            this.groupBy = groupBy;
            return this;
        }
        
        protected SimpleQueryBuilder orderBy(String orderBy){
            this.orderBy = orderBy;
            return this;
        }
        
        protected SimpleQueryBuilder leftJoin(String table, String on){
            this.leftJoin = "LEFT JOIN " + table + " ON " + on;
            return this;
        }
        
        protected SimpleQueryBuilder limit(int limit){
            this.limit = limit;
            return this;
        }
        
        protected SimpleQueryBuilder addWhere(String where, Object param){
            this.wheres.add(where);
            this.params.add(param);
            return this;
        }
        
        protected ResultSet execute() throws SQLException{
            String queryBuilder = select;
            
            if(leftJoin != null){
                queryBuilder += " " + leftJoin;
            }
            
            if(wheres.size() > 0){
                queryBuilder += " WHERE";
                for(int x = 0; x < wheres.size(); x++){
                    if(x > 0) {
                        queryBuilder += " AND";
                    }
                    queryBuilder += " " + wheres.get(x);
                }
            }
            
            if(groupBy != null){
                queryBuilder += " GROUP BY " + groupBy;
            }
            
            if(orderBy != null){
                queryBuilder += " ORDER BY " + orderBy;
            }
            
            if(limit != -1){
                queryBuilder += " LIMIT " + limit;
            }
            
            PreparedStatement ps = databaseProvider.getConnection().prepareStatement(queryBuilder);
            
            int index = 1;
            for(Object param : params){
                ps.setObject(index++, param);
            }
            
            return ps.executeQuery();
        }
    }
}
