package com.ylz.jckdemo.service;
import javax.jcr.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.jackrabbit.core.jndi.RegistryHelper;
import org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Hashtable;

@Service
public class DbRepositoryService implements InitializingBean {

    private Session jcrSession;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
