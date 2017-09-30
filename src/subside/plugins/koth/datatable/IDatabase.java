package subside.plugins.koth.datatable;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabase {
    public Connection getConnection() throws SQLException;
}