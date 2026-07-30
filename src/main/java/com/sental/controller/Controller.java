package com.sental.controller;


import com.sental.service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Controller implements ControllerInterface{

    @Autowired
    private ServiceInterface service;

    @Override
    public String execute() {
        return service.execute();
    }
}