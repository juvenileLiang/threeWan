package com.els;

import com.els.service.Computer;
import com.els.until.ApplicationContext;
import com.els.until.CrazyitXMLApplicationContext;

public class Test {
    public static void main(String[] args) throws Exception{
        ApplicationContext app = new CrazyitXMLApplicationContext("E:\\wegame\\mySpringIoc\\src\\main\\resources\\beans.xml");
        Computer com = (Computer) app.getBean("computer");
        com.useOut();
        System.out.println(app.getBean("now"));
    }
}
