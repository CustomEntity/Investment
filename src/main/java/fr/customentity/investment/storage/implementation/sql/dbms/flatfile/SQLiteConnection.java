package fr.customentity.investment.storage.implementation.sql.dbms.flatfile;

import fr.customentity.investment.storage.StorageCredentials;
import fr.customentity.investment.storage.implementation.sql.dbms.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConnection implements ConnectionFactory {

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public String getDbmsName() {
        return "SQLite";
    }

    @Override
    public void init(StorageCredentials storageCredentials) {

    }

    @Override
    public void shutdown() {

    }
}
