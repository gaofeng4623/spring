package com.spring.context.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.spring.beans.aware.ApplicationContextAware;
import com.spring.beans.aware.ServletContextAware;
import com.spring.beans.common.SourceItem;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.common.utils.XmlParseUtil;
import com.spring.beans.factory.IocBaseContext;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.factory.context.ApplicationContext;
import com.spring.beans.resource.ClassPathResource;
import com.spring.context.WebApplicationContext;


public class ContextLoader extends XmlParseUtil {

    private XmlBeanFactory beanFactory;
    private List<String> beanIdList;

    public ContextLoader() {
        this.beanIdList = new ArrayList<String>();
    }

    public XmlBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(XmlBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public List<String> getBeanList() {
        return beanIdList;
    }

    public void initWebApplicationContext(ServletContext sc) throws Exception {
        String[] xmlpaths = null;
        String realPath = null;
        XmlBeanFactory factory = null;
        String path = sc.getInitParameter(Consts.contextConfigLocation);
        if (path != null) {
            if (path.indexOf(",") > 0) {
                String[] arr = path.split(",");
                xmlpaths = new String[arr.length];
                for (int i = 0; i < arr.length; i++) {
                    String str = arr[i].replaceAll("\r\n", "");
                    str = str.trim();
                    realPath = sc.getRealPath(str);
                    xmlpaths[i] = realPath;
                }
                factory = new XmlBeanFactory(new ClassPathResource(xmlpaths));
                fillContextStack(xmlpaths, factory);
            } else {
                realPath = sc.getRealPath(path.trim());
                factory = new XmlBeanFactory(new ClassPathResource(realPath));
                fillContextStack(realPath, factory);
            }
        } else {
            realPath = sc.getRealPath(Consts.AppliactionContext);
            factory = new XmlBeanFactory(new ClassPathResource(realPath));
            fillContextStack(realPath, factory);
        }
        /********加载web应用的上下文*********/
        WebApplicationContext wac = createWebApplicationContext(factory, sc);
        sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        this.beanFactory = factory;
    }


    /**
     * 初始化上下文堆栈
     * 装载所有的Advisors适配器和基于注解的通知
     *
     * @param object
     * @param factory
     */
    public void fillContextStack(Object object, XmlBeanFactory factory) {
        Document doc = null;
        try {
            ClassPathResource source = new ClassPathResource();
            if (object instanceof String[]) {
                String[] xmls = (String[]) object;
                for (String e : xmls) {
                    doc = source.getDoc(e);
                    fillAdvisors(doc, factory);
                }
            } else if (object instanceof String) {
                String xml = object.toString();
                doc = source.getDoc(xml);
                fillAdvisors(doc, factory);
            }
            if (factory.aspectScanable()) {
                List<?> annotationAdvices = factory.getAspectsByAnnotation(factory.getBasePackage());
                System.out.println("aspect通知个数 -- " + annotationAdvices.size());
                factory.setAnnotationAdvices(annotationAdvices); //装载注解通知
            } else {
                //此处的代理需要把xml配置的切面加入,标签aop-config，同上
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 装载所有的AOP适配器
     * @param beans
     * @param factory
     * @throws Exception
     */
    private void fillAdvisors(Node beans, XmlBeanFactory factory) throws Exception {
        String sign = null;
        Node bean = null;
        String className = "";
        List<Node> nodes = getNodeList("beans/bean", beans);
        for (int i = 0; i < nodes.size(); i++) {
            bean = (Node) nodes.get(i);
            className = getAttributeOf("class", bean);
            try {
                Class<?> type = Class.forName(className);
                if (IocBaseContext.isPointcutAdvisor(type)) {
                    SourceItem si = factory.createBeanByConfigration(bean, true);
                    if (si != null) factory.getAdvisors().add(si.getSource()); //装载适配器
                }
                if (isDelayed(bean, beans)) continue;
            } catch (ClassNotFoundException e) {
                System.err.println("fillResult----" + e.getMessage());
            }
            sign = getSignOfElementBean(bean);
            this.beanIdList.add(sign); //装载beanId
        }
    }


    /******初始化Spring的应用上下文
     * @throws Exception *******/
    public WebApplicationContext createWebApplicationContext(XmlBeanFactory factory, ServletContext sc) throws Exception {
        String key = "";
        SourceItem result = null;
        factory.setContextModel(true); //启动context容器模式
        WebApplicationContext wact = new WebApplicationContext();
        for (Iterator<String> it = getBeanList().iterator(); it.hasNext(); ) {
            key = it.next();
            factory.getBean(key);
            result = factory.getBeanInfo(key);
            if (result == null) continue;
            reflectContextAware(result, sc, wact);
            if (wact.isHandleMapping(result.getProtypeClass())) {
                wact.addHandleMapping(result.getObject());
            }
        }
        if (factory.iocScanable()) { //开启IOC注解扫描
            Map<String, SourceItem> annotations = factory.getAnnotationBeansByDirectory(factory.getBasePackage());
            for (Iterator<String> it = annotations.keySet().iterator(); it.hasNext(); ) {
                key = it.next();
                SourceItem si = annotations.get(key);
                reflectContextAware(si, sc, wact);
                IocBaseContext.doAopFilter(si, factory); //集成所有的注解代理,注意此处扫描的bean有可能是已经缓存的代理bean,慎重
            }
        }
        wact.setBeanFactory(factory);
        wact.setBeanIdList(this.beanIdList);
        wact.setControllers(factory.getControllers());
        wact.setExceptionHandles(factory.getExceptionHandles());
        return wact;
    }


    /***
     * 映射上下文接口
     * @param si
     * @param sc
     * @param context
     */
    public void reflectContextAware(SourceItem si, ServletContext sc, ApplicationContext context) {
        Object bean = si.getObject();
        if (bean != null && (bean instanceof ServletContextAware)) {
            ((ServletContextAware) bean).setServletContext(sc);
        }
        if (bean != null && (bean instanceof ApplicationContextAware)) {
            ((ApplicationContextAware) bean).setApplicationContext(context);
        }
    }


    /**
     * 判断是否需要延迟加载
     *
     * @param e
     * @return
     */
    public boolean isDelayed(Node e, Node doc) {
        String scope = getAttributeOf(Consts.scope, e);
        String singleton = getAttributeOf(Consts.singleton, e);
        String lazy = getAttributeOf(Consts.lazyinit, e);

        boolean isProto = singleton.equalsIgnoreCase("false")
                || scope.equalsIgnoreCase(Consts.prototype);
        Node root = getChildrenByTagName("beans", doc);
        String defaultLazy = getAttributeOf(Consts.defaultLazyInit, root);

        boolean isLazy = "true".equals(lazy) || "true".equals(defaultLazy);
        return isProto || isLazy;
    }
}
