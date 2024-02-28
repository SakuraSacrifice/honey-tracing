package com.honey.tracing.database.postprocessor;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

public class SqlSessionFactoryBeanPostProcessor implements BeanPostProcessor {

    private final List<Interceptor> interceptors;

    public SqlSessionFactoryBeanPostProcessor(List<Interceptor> interceptors) {
        if (null == interceptors) {
            this.interceptors = new ArrayList<>();
        } else {
            this.interceptors = interceptors;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            for (Interceptor interceptor : interceptors) {
                ((SqlSessionFactory) bean).getConfiguration().addInterceptor(interceptor);
            }
        }
        return bean;
    }

}