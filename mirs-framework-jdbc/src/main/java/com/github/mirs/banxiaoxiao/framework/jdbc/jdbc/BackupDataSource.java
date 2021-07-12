package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class BackupDataSource extends HikariDataSource {

    private Logger logger = LoggerFactory.getLogger(BackupDataSource.class);

    private BackupDataSource backup;

    private long getConnectionFailTime = 0;

    private long trySelfConnectionCycle = 300000;

    private long nextTrySelfConnectTime = 0;

    private int trySelfCount = 0;

    private Throwable lastException;

    public BackupDataSource() {
        super();
        setConnectionTimeout(10000);
    }

    public void setBackup(BackupDataSource backup) {
        this.backup = backup;
    }

    private void resetSelfConnectFail() {
        getConnectionFailTime = 0;
        nextTrySelfConnectTime = 0;
        trySelfCount = 0;
    }

    private Connection getSuperConnection() throws SQLException {
        return super.getConnection();
    }

    public Connection getConnection() throws SQLException {
        Connection connection = null;
        BackupDataSource ds = choiceDataSource();
        boolean choiceSelf = ds.equals(this);
        connection = getConnection(ds);
        if (connection == null) {
            BackupDataSource otherDs = getReverseDataSource(ds);
            boolean same = otherDs.equals(ds);
            if(!same) {
                choiceSelf = otherDs.equals(this);
                connection = getConnection(otherDs);
            }
        }
        if (connection == null) {
            SQLException exception = createLastSqlException();
            throw exception;
        } else {
            if (choiceSelf) {
                resetSelfConnectFail();
            }
        }
        return connection;
    }
    
    private BackupDataSource getReverseDataSource(BackupDataSource ds) {
        boolean choiceSelf = ds.equals(this);
        if(choiceSelf) {
            if(backup != null) {
                return backup;
            } else {
                return this;
            }
        } else {
            return this;
        }
    }

    private BackupDataSource choiceDataSource() {
        boolean choiceSelfConnect = true;
        if (getConnectionFailTime > 0) {
            long intervalTime = System.currentTimeMillis() - getConnectionFailTime;
            if (intervalTime >= nextTrySelfConnectTime) {
                trySelfCount++;
                nextTrySelfConnectTime += (trySelfConnectionCycle * trySelfCount);
                choiceSelfConnect = true;
            } else if (backup != null) {
                choiceSelfConnect = false;
            }
        }
        if (choiceSelfConnect) {
            return this;
        } else if (backup != null) {
            return backup;
        } else {
            return this;
        }
    }

    private Connection getConnection(BackupDataSource ds) {
        try {
            return ds.getSuperConnection();
        } catch (Throwable e) {
            if (ds.equals(this) && getConnectionFailTime <= 0) {
                getConnectionFailTime = System.currentTimeMillis();
            }
            logger.warn("get backup datasource {} connection fail", backup);
            lastException = e;
            return null;
        }
    }

    private SQLException createLastSqlException() {
        if (lastException != null) {
            if (lastException instanceof SQLException) {
                return (SQLException) lastException;
            } else {
                return new SQLException(lastException);
            }
        } else {
            return new SQLException("no connection can use");
        }
    }

    public Connection getConnection(String userName, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
