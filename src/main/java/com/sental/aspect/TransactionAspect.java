package com.sental.aspect;

import com.sental.database.ConnectionHolder;
import com.sental.exception.TransactionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Aspect
@Component
public class TransactionAspect {

    private final ConnectionHolder connectionHolder;

    @Autowired
    public TransactionAspect(ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    @Around("@annotation(com.sental.annotation.Transaction)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Connection connection = connectionHolder.getTransactionConnection();
        boolean startedHere = false;

        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                startedHere = true;
            }
        } catch (SQLException e) {
            throw new TransactionException("fail to start transaction", e);
        }

        try {
            Object result = joinPoint.proceed();
            if (startedHere) {
                commitTransaction(connection);
            }
            return result;
        } catch (RuntimeException e) {
            if (startedHere) {
                rollbackTransaction(connection);
            }
            throw e;
        }
    }

    private void commitTransaction(Connection connection) {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException("fail commit transaction", e);
        }
    }

    private void rollbackTransaction(Connection connection) {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new TransactionException("fail rollback transaction", e);
        }
    }
}