package com.sental;

import com.sental.controller.Controller;
import com.sental.core.Context;

public class App {

    public static void main(String[] args) {
        Context.run(App.class);
        Controller controller = (Controller) Context.getBean(Controller.class);
        String result = controller.execute();
        System.out.println(result);
    }
}
