package com.sental.service;

import com.sental.annotations.Autowire;
import com.sental.annotations.Component;
import com.sental.database.Database;
import com.sental.database.DatabaseInterface;

@Component
public class Service implements ServiceInterface{

    private DatabaseInterface database;

    @Autowire
    public void setDatabase(DatabaseInterface database) {
        this.database = database;
    }

    @Override
    public String execute() {
        return database.execute();
    }
}