package subside.plugins.koth.datatable;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabase {
    public abstract Connection getConnection() throws SQLException;
}