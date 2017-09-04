package com.spring.context;

import com.spring.beans.aware.ApplicationContextAware;
import com.spring.beans.factory.context.ApplicationContext;

public class SpringContextUtil  implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;  
    
      
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;  
    }
  
      
    public ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }
      
    public static <T> T getBean(String name) throws Exception {  
        return (T) applicationContext.getBean(name);  
    }
    public static <T> T getBean(Class<?> requiredType) throws Exception {  
        return (T) applicationContext.getBean(requiredType);  
    }


}
