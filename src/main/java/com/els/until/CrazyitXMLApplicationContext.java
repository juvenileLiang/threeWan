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
    //���׶�������
    private Map<String,Object> objpool= Collections.synchronizedMap(new HashMap<String,Object>());
    //�ĵ�
    private Document document;
    //��Ԫ��
    private Element root;
    //��ʼ������
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
        //�õ�����bean
        for (Object obj:root.elements()){
            Element element = (Element) obj;
            //�õ�id
            String id=element.attributeValue("id");
            //�õ�class
            String clname=element.attributeValue("class");
            //�õ�scope
            String sco = element.attributeValue("scope");
            if (sco!=null&&sco!="singleton"){
                objpool.put(id,clname);
            }else {
                objpool.put(id,Class.forName(clname).getConstructor().newInstance());
            }
        }
    }
    public void initProp()throws Exception{
        //�õ�����bean
        for (Object obj:root.elements()){
            Element element = (Element) obj;
            //�õ�id
            String id=element.attributeValue("id");
            //�õ�class
            String clname=element.attributeValue("class");
            //�õ�scope
            String sco = element.attributeValue("scope");
            if (sco!=null&&sco!="singleton"){

            }else {
                //�õ�����
                Object bean =objpool.get(id);
                if(bean instanceof Computer){
                    bean=(Computer)bean;
                }
                //������һ��Ԫ��
                for(Object prop:element.elements()){
                    Element propEle = (Element)prop;
                    //�õ�name
                    String propName = propEle.attributeValue("name");
                    //�õ�value
                    String propValue = propEle.attributeValue("value");
                    //�õ�ref
                    String propRef = propEle.attributeValue("ref");
                    //��name��д���д
                    String propNameNew=propName.substring(0,1).toUpperCase()+propName.substring(1,propName.length());
                    //���valueֵ����
                    if(propValue != null&&propValue.length()>0){
                        Method setter = bean.getClass().getMethod("set"+propNameNew,String.class);
                        setter.invoke(bean,propValue);
                    }
                    //���ref����
                    if(propRef != null&&propRef.length()>0){
                        Object target = objpool.get(propRef);
                        //��������ڻ�����String(���ǵ���)
                        if(target==null||target.getClass()==String.class){

                        }
                        Method setter =null;
                        //����target��ʵ�ֵ�ȫ���ӿ�
                        for(Class superInterface : target.getClass().getInterfaces()){
                            try {
                                setter = bean.getClass().getMethod("set"+propNameNew,superInterface);
                                break;
                            }catch (NoSuchMethodException ex){
                                continue;
                            }
                        }
                        //���set������ȻΪ�գ�˵��set�����ò����õ�ʵ����
                        if(setter==null){
                            setter = bean.getClass().getMethod("set"+propNameNew,target.getClass());
                        }
                        //ִ��setter
                        setter.invoke(bean,target);
                    }

                }
            }

        }
    }
}
