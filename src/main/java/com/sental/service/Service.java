package com.sental.service;


import com.sental.database.DatabaseInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service implements ServiceInterface{

    private DatabaseInterface database;

    @Autowired
    public void setDatabase(DatabaseInterface database) {
        this.database = database;
    }

    @Override
    public String execute() {
        return database.execute();
    }
}