package com.sental.core;

public class Context {

    private static DIContainer container;

    public static void run(Class<?> mainClass) {
        container = new DIContainer();
        container.init(mainClass.getPackage().getName());
    }

    public static Object getBean(Class<?> clazz) {
        return container.getBean(clazz);
    }
}