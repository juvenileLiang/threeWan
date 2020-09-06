package com.els.until;

public interface ApplicationContext {
    Object getBean(String name) throws Exception;
}
