package com.els.until;

import com.els.service.Computer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CrazyitXMLApplicationContext implements ApplicationContext{
    //简易对象容器
    private Map<String,Object> objpool= Collections.synchronizedMap(new HashMap<String,Object>());
    //文档
    private Document document;
    //根元素
    private Element root;
    //初始化容器
    public CrazyitXMLApplicationContext(String filePath)throws Exception{
        SAXReader reader = new SAXReader();
        document = reader.read(new File(filePath));
        root=document.getRootElement();
        initBeans();
        initProp();
    }
    @Override
    public Object getBean(String name)throws Exception{
        Object obj = objpool.get(name);
        if(obj.getClass()!=String.class){
            return obj;
        }else {
            return Class.forName((String) obj).getConstructor ().newInstance();
        }
    }
    public void initBeans()throws Exception{
        //拿到所有bean
        for (Object obj:root.elements()){
            Element element = (Element) obj;
            //拿到id
            String id=element.attributeValue("id");
            //拿到class
            String clname=element.attributeValue("class");
            //拿到scope
            String sco = element.attributeValue("scope");
            if (sco!=null&&sco!="singleton"){
                objpool.put(id,clname);
            }else {
                objpool.put(id,Class.forName(clname).getConstructor().newInstance());
            }
        }
    }
    public void initProp()throws Exception{
        //拿到所有bean
        for (Object obj:root.elements()){
            Element element = (Element) obj;
            //拿到id
            String id=element.attributeValue("id");
            //拿到class
            String clname=element.attributeValue("class");
            //拿到scope
            String sco = element.attributeValue("scope");
            if (sco!=null&&sco!="singleton"){

            }else {
                //拿到单例
                Object bean =objpool.get(id);
                if(bean instanceof Computer){
                    bean=(Computer)bean;
                }
                //遍历下一个元素
                for(Object prop:element.elements()){
                    Element propEle = (Element)prop;
                    //拿到name
                    String propName = propEle.attributeValue("name");
                    //拿到value
                    String propValue = propEle.attributeValue("value");
                    //拿到ref
                    String propRef = propEle.attributeValue("ref");
                    //将name首写变大写
                    String propNameNew=propName.substring(0,1).toUpperCase()+propName.substring(1,propName.length());
                    //如果value值存在
                    if(propValue != null&&propValue.length()>0){
                        Method setter = bean.getClass().getMethod("set"+propNameNew,String.class);
                        setter.invoke(bean,propValue);
                    }
                    //如果ref存在
                    if(propRef != null&&propRef.length()>0){
                        Object target = objpool.get(propRef);
                        //如果不存在或则是String(不是单例)
                        if(target==null||target.getClass()==String.class){

                        }
                        Method setter =null;
                        //遍历target所实现得全部接口
                        for(Class superInterface : target.getClass().getInterfaces()){
                            try {
                                setter = bean.getClass().getMethod("set"+propNameNew,superInterface);
                                break;
                            }catch (NoSuchMethodException ex){
                                continue;
                            }
                        }
                        //如果set方法依然为空，说明set方法得参数用的实现类
                        if(setter==null){
                            setter = bean.getClass().getMethod("set"+propNameNew,target.getClass());
                        }
                        //执行setter
                        setter.invoke(bean,target);
                    }

                }
            }

        }
    }
}
