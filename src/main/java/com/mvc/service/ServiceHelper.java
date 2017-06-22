package com.mvc.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by llc on 16/8/18.
 */
public class ServiceHelper {
    private static NodeService nodeService;

    static {
        ApplicationContext ctx= new ClassPathXmlApplicationContext("spring-mybatis.xml");
        nodeService = (NodeService) ctx.getBean("NodeService");
    }

    public static NodeService getNodeService(){
        return nodeService;
    }

}
