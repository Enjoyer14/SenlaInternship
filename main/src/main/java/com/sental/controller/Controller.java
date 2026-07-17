package com.sental.controller;

import com.sental.annotations.Autowire;
import com.sental.annotations.Component;
import com.sental.service.ServiceInterface;

@Component
public class Controller implements ControllerInterface{

    @Autowire
    private ServiceInterface service = null;

    @Override
    public String execute() {
        return service.execute();
    }
}