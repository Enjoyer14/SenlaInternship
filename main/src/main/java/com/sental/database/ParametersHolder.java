package com.sental.database;

import com.sental.annotations.Component;
import com.sental.annotations.Value;

@Component
public class ParametersHolder {

    @Value("my.param.db")
    private String someText;

    public String getSomeText() {
        return someText;
    }
}