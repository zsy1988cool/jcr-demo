package com.ylz.jckdemo.repository;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class RepositoryBuilder implements InitializingBean,ApplicationContextAware {
    private String repositoryXml;
    private ApplicationContext applicationContext;
    private String repositoryDatasourceName;
    public static String databaseType;
    public static DataSource datasource;
    private RepositoryImpl repository;

    public RepositoryImpl getRepository() {
        return repository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isNotBlank(repositoryDatasourceName)){
            RepositoryBuilder.datasource=(DataSource)this.applicationContext.getBean(repositoryDatasourceName);
        }

        if(repository!=null){
            repository.shutdown();
        }

        if(RepositoryBuilder.datasource!=null){
            if(RepositoryBuilder.databaseType==null){
                throw new Exception("You need config \"urule.repository.databasetype\" property when use spring datasource!");
            }
            initRepositoryFromSpringDatasource();
        }
    }

    private void initRepositoryFromSpringDatasource() throws Exception{
        System.out.println("Init repository from spring datasource ["+repositoryDatasourceName+"] with database type ["+RepositoryBuilder.databaseType+"]...");
        String xml="classpath:"+RepositoryBuilder.databaseType+".xml";
        initRepositoryByXml(xml);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getRepositoryXml() {
        return repositoryXml;
    }

    public void setRepositoryXml(String repositoryXml) {
        this.repositoryXml = repositoryXml;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getRepositoryDatasourceName() {
        return repositoryDatasourceName;
    }

    public void setRepositoryDatasourceName(String repositoryDatasourceName) {
        this.repositoryDatasourceName = repositoryDatasourceName;
    }

    public static String getDatabaseType() {
        return databaseType;
    }

    public static void setDatabaseType(String databaseType) {
        RepositoryBuilder.databaseType = databaseType;
    }

    public static DataSource getDatasource() {
        return datasource;
    }

    public static void setDatasource(DataSource datasource) {
        RepositoryBuilder.datasource = datasource;
    }

//    private PersistenceManagerConfig buildPersistenceManagerConfig(){
//        Properties prop=new Properties();
//        BeanConfig beanConfig=new BeanConfig("org.apache.jackrabbit.core.persistence.bundle.BundleFsPersistenceManager",prop);
//        PersistenceManagerConfig persistenceManagerConfig=new PersistenceManagerConfig(beanConfig);
//        return persistenceManagerConfig;
//    }
//
//    private SecurityManagerConfig buildSecurityManagerConfig(){
//        Properties prop=new Properties();
//        BeanConfig beanConfig=new BeanConfig("org.apache.jackrabbit.core.security.simple.SimpleSecurityManager",prop);
//        SecurityManagerConfig securityManagerConfig=new SecurityManagerConfig(beanConfig,"default",null);
//        return securityManagerConfig;
//    }
//
//    private AccessManagerConfig buildAccessManagerConfig(){
//        Properties prop=new Properties();
//        BeanConfig beanConfig=new BeanConfig("org.apache.jackrabbit.core.security.simple.SimpleAccessManager",prop);
//        AccessManagerConfig accessManagerConfig=new AccessManagerConfig(beanConfig);
//        return accessManagerConfig;
//    }
//
//    private LoginModuleConfig buildLoginModuleConfig(){
//        Properties prop=new Properties();
//        prop.put("anonymousId", "anonymous");
//        prop.put("adminId", "admin");
//        BeanConfig beanConfig=new BeanConfig("org.apache.jackrabbit.core.security.simple.SimpleLoginModule",prop);
//        LoginModuleConfig loginModuleConfig=new LoginModuleConfig(beanConfig);
//        return loginModuleConfig;
//    }
//
//    private SecurityConfig buildSecurityConfig(){
//        SecurityConfig securityConfig=new SecurityConfig("uruleRepoSecurity",buildSecurityManagerConfig(),buildAccessManagerConfig(),buildLoginModuleConfig());
//        return securityConfig;
//    }
//
//    private void initDefaultRepository()throws Exception {
//        SecurityConfig securityConfig = buildSecurityConfig();
//    }

    private void initRepositoryByXml(String xml)throws Exception {
        //log.info("Build repository from user custom xml file...");
        InputStream inputStream=null;
        try{
            inputStream=this.applicationContext.getResource(xml).getInputStream();
            String tempRepoHomeDir=System.getProperty("java.io.tmpdir");
            if(StringUtils.isNotBlank(tempRepoHomeDir) && tempRepoHomeDir.length()>1){
                if(tempRepoHomeDir.endsWith("/") || tempRepoHomeDir.endsWith("\\")){
                    tempRepoHomeDir+="ylz-rule-temp-repo-home/";
                }else{
                    tempRepoHomeDir+="/ylz-rule-temp-repo-home/";
                }
                File tempDir=new File(tempRepoHomeDir);
                clearTempDir(tempDir);
            }else{
                tempRepoHomeDir="";
            }
            RepositoryConfig repositoryConfig = RepositoryConfig.create(inputStream,tempRepoHomeDir);
            repository= RepositoryImpl.create(repositoryConfig);
        }finally{
            if(inputStream!=null){
                inputStream.close();
            }
        }
    }

    private void clearTempDir(File file){
        if(file.isDirectory()){
            for(File childFile:file.listFiles()){
                clearTempDir(childFile);
            }
        }
        file.delete();
    }
}
