package fr.customentity.investment.storage.implementation.sql.dbms;

import fr.customentity.investment.storage.StorageCredentials;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    Connection getConnection() throws SQLException;

    String getDbmsName();

    void init(StorageCredentials storageCredentials);

    void shutdown();
}
