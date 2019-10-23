package com.jtfu.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpittrWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
 
    /*加载后端的中间层和数据层组件*/
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class};
    }
 
    /*加载web组件，控制器、试图解析器、处理器映射*/
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }
 
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}