package com.sental.core;

import com.sental.annotations.*;

import com.sental.exceptions.*;
import org.reflections.Reflections;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class DIContainer {

    private final Map<Class<?>, Object> beans = new HashMap<>();
    private final Properties properties = new Properties();
    private Set<Class<?>> componentClasses;

    public void init(String basePackage) {
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить application properties", e);
        }

        Reflections reflections = new Reflections(basePackage);
        componentClasses = reflections.getTypesAnnotatedWith(Component.class);//сканиурем наш пакет
        for(Class<?> clazz : componentClasses) {
            createBean(clazz);
        }

        for(Object bean : beans.values()) {
            injectValues(bean);
            injectFields(bean);
            injectSetters(bean);
        }
    }

    public Object getBean(Class<?> clazz) {
        if(clazz.isInterface()){ // если запросили интерфейс то ищем его реализацию
            for(Object bean : beans.values()) {
                if(clazz.isInstance(bean)) {
                    return bean;
                }
            }
        }
        return beans.get(clazz);
    }

    private void createBean(Class<?> clazz) {
        if(clazz.isInterface()){
            clazz = findImplementation(clazz);
        }
        if(beans.containsKey(clazz)) {
            return;
        }
        try {
            Constructor<?> autowiredConstructor = null;
            for(Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if(constructor.isAnnotationPresent(Autowire.class)){
                    autowiredConstructor = constructor;
                    break;
                }
            }

            Object instance;
            if(autowiredConstructor != null) {
                Class<?>[] paramTypes = autowiredConstructor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    createBean(paramTypes[i]);
                    params[i] = getBean(paramTypes[i]);
                }
                boolean isAccessible = autowiredConstructor.isAccessible();
                autowiredConstructor.setAccessible(true);
                try {
                    autowiredConstructor.setAccessible(true);
                    instance = autowiredConstructor.newInstance(params);
                } finally {
                    if (!isAccessible) {
                        autowiredConstructor.setAccessible(false);
                    }
                }
            } else {
                Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
                boolean isAccessible = defaultConstructor.isAccessible();
                try {
                    defaultConstructor.setAccessible(true);
                    instance = defaultConstructor.newInstance();
                } finally {
                    if (!isAccessible) {
                        defaultConstructor.setAccessible(false);
                    }
                }
            }
            beans.put(clazz, instance);
        } catch (Exception e) {
            throw new BeanCreationException("Ошибка создания бина" + e);
        }
    }

    private Class<?> findImplementation(Class<?> interfaceClass) {
        for (Class<?> clazz : componentClasses) {
            if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface()) {
                return clazz;
            }
        }
        throw new ImplementationNotFoundException("Не найдена реализация для интерфейса: " + interfaceClass.getName());
    }

    private void injectValues(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                String key = field.getAnnotation(Value.class).value();
                String val = properties.getProperty(key);
                if (val != null) {
                    boolean isAccessible = field.isAccessible();
                    try {
                        field.setAccessible(true);
                        field.set(instance, val);
                    } catch (Exception e) {
                        throw new PropertyInjectionException("Не удалось внедрить значение свойства '" + key + "' в поле " + field.getName() + " " + e);
                    } finally {
                        if(!isAccessible) field.setAccessible(false);
                    }
                }
            }
        }
    }

    private void injectFields(Object instance) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowire.class)) {
                Object dep = getBean(field.getType());
                if (dep == null) {
                    throw new BeanNotFoundException("Не найден бин для внедрения в поле " + field.getName() + " класса " + instance.getClass().getName());
                }
                try {
                    field.setAccessible(true);
                    field.set(instance, dep);
                } catch (Exception e) {
                    throw new DependencyInjectionException("Не удалось внедрить зависимость в поле " + field.getName() + " " + e);
                }
            }
        }
    }

    private void injectSetters(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Autowire.class)) {
                Class<?> paramType = method.getParameterTypes()[0];
                Object dep = getBean(paramType);
                if (dep == null) {
                    throw new BeanNotFoundException("Не найден бин для внедрения в сеттер " + method.getName() + " класса " + instance.getClass().getName());
                }
                try {
                    method.setAccessible(true);
                    method.invoke(instance, dep);
                } catch (Exception e) {
                    throw new SetterInjectionException("Не удалось внедрить зависимость через сеттер " + method.getName() + " " + e);
                }
            }
        }
    }
}