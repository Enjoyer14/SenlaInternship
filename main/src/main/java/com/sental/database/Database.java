package com.sental.database;

import com.sental.annotations.Autowire;
import com.sental.annotations.Component;

@Component
public class Database implements DatabaseInterface {

    @Autowire
    private ParametersHolder parametersHolder;

    @Override
    public String execute() {
        return parametersHolder.getSomeText();
    }
}