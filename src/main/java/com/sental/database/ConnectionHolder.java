package com.sental.database;

import com.sental.exception.TransactionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Component
public class ConnectionHolder {

    private final long MAX_IDLE_TIME = 300_000L;
    private final long CLEANUP_INTERVAL = 300_000L;
    private final DataSource dataSource;
    private final Map<Long, List<ThreadConnection>> connectionsByThread = new HashMap<>();

    public ConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static class ThreadConnection {
        private final Connection connection;
        private final boolean transactional;
        private final Thread thread;
        private Long usedTime;

        private ThreadConnection(Connection connection, boolean transactional) {
            this.connection = connection;
            this.transactional = transactional;
            this.thread = Thread.currentThread();
            this.usedTime = System.currentTimeMillis();

        }
    }

    public synchronized Connection getConnection() {
        long threadId = Thread.currentThread().threadId();
        List<ThreadConnection> list = connectionsByThread.get(threadId);
        if (list != null) {
            list.removeIf(tc -> !tc.transactional && isClosed(tc.connection));
            Connection txConnection = findTransactionalConnection(list);
            if (txConnection != null) {
                return txConnection;
            }

            if (!list.isEmpty()) {
                ThreadConnection tc = list.getFirst();
                tc.usedTime = System.currentTimeMillis();
                return tc.connection;
            }
            connectionsByThread.remove(threadId);
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
            if (!tc.transactional) {
                continue;
            }
            if (isClosed(tc.connection)) {
                throw new TransactionException("transaction connection is closed");
            }
            tc.usedTime = System.currentTimeMillis();
            return tc.connection;
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

    private boolean isStale(ThreadConnection tc) {
        if (isClosed(tc.connection)) {
            return true;
        }

        if (!tc.thread.isAlive()) {
            if (tc.transactional) {
                rollbackConnection(tc.connection);
            }
            closeConnection(tc.connection);
            return true;
        }

        long idle = System.currentTimeMillis() - tc.usedTime;
        return !tc.transactional && idle > MAX_IDLE_TIME;
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

    @Scheduled(fixedDelay = CLEANUP_INTERVAL)
    public synchronized void cleanupConnections() {
        connectionsByThread.values().forEach(list -> list.removeIf(this::isStale));
        connectionsByThread.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {}
    }

    private void rollbackConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {}
    }

    public synchronized void releaseCurrentThread() {
        long threadId = Thread.currentThread().threadId();
        List<ThreadConnection> list = connectionsByThread.remove(threadId);
        if (list != null) {
            list.forEach(tc -> closeConnection(tc.connection));
        }
    }

}