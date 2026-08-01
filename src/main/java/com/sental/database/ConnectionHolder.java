package com.sental.database;

import com.sental.exception.TransactionException;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConnectionHolder {
    private final DataSource dataSource;
    private final Map<Long, List<ThreadConnection>> connectionsByThread = new HashMap<>();

    public ConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static class ThreadConnection {
        private final Connection connection;
        private final boolean transactional;

        private ThreadConnection(Connection connection, boolean transactional) {
            this.connection = connection;
            this.transactional = transactional;
        }
    }

    public synchronized Connection getConnection() {
        long threadId = Thread.currentThread().threadId();
        List<ThreadConnection> list = connectionsByThread.get(threadId);
        if (list != null) {
            list.removeIf(tc -> isClosed(tc.connection));
            Connection txConnection = findTransactionalConnection(list);
            if (txConnection != null) {
                return txConnection;
            }

            for (ThreadConnection tc : list) {
                if (!isClosed(tc.connection)) {
                    return tc.connection;
                }
            }
            if (list.isEmpty()) {
                connectionsByThread.remove(threadId);
            }
        }
        return createConnection(false);
    }

    public synchronized Connection getTransactionConnection() {
        long threadId = Thread.currentThread().threadId();
        List<ThreadConnection> list = connectionsByThread.get(threadId);
        if (list != null) {
            Connection txConnection = findTransactionalConnection(list);
            if (txConnection != null) {
                return txConnection;
            }
        }

        return createConnection(true);
    }

    private Connection findTransactionalConnection(List<ThreadConnection> list) {
        for (ThreadConnection tc : list) {
            if (tc.transactional) {
                if (isClosed(tc.connection)) {
                    throw new TransactionException("transaction connection is closed");
                }
                return tc.connection;
            }
        }
        return null;
    }

    private Connection createConnection(boolean transactional) {
        try {
            Connection connection = dataSource.getConnection();
            long threadId = Thread.currentThread().threadId();

            List<ThreadConnection> list = connectionsByThread.get(threadId);
            if (list == null) {
                list = new ArrayList<>();
                connectionsByThread.put(threadId, list);
            }

            list.add(new ThreadConnection(connection, transactional));
            return connection;
        } catch (SQLException e) {
            throw new TransactionException("fail to create connection", e);
        }
    }

    private boolean isClosed(Connection connection) {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        for (List<ThreadConnection> list : connectionsByThread.values()) {
            for (ThreadConnection tc : list) {
                try {
                    if (tc.connection != null && !tc.connection.isClosed()) {
                        tc.connection.close();
                    }
                } catch (SQLException e) {}
            }
        }
        connectionsByThread.clear();
    }


}