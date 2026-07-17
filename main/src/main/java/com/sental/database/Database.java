package com.sental.database;

import com.sental.annotations.Autowire;
import com.sental.annotations.Component;

@Component
public class Database implements DatabaseInterface {

    private final ParametersHolder parametersHolder;

    @Autowire
    public Database(ParametersHolder parametersHolder) {
        this.parametersHolder = parametersHolder;
    }

    @Override
    public String execute() {
        return parametersHolder.getSomeText();
    }
}