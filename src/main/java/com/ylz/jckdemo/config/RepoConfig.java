package com.ylz.jckdemo.config;

import com.ylz.jckdemo.repository.RepositoryBuilder;
import org.apache.commons.dbcp.BasicDataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "urule.repository")
public class RepoConfig {
    String datasourcename;
    String databasetype;

    public String getDatasourcename() {
        return datasourcename;
    }

    public void setDatasourcename(String datasourcename) {
        this.datasourcename = datasourcename;
    }

    public String getDatabasetype() {
        return databasetype;
    }

    public void setDatabasetype(String databasetype) {
        this.databasetype = databasetype;
    }

    @Bean(name = "mysqlDatasource")
    DataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/urule_test?useUnicode=true&amp;characterEncoding=UTF-8");
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("qzd158802");
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxActive(10);
        basicDataSource.setMaxWait(1000);
        basicDataSource.setRemoveAbandonedTimeout(60);
        basicDataSource.setRemoveAbandoned(true);
        basicDataSource.setLogAbandoned(true);

        return basicDataSource;
    }

    @Bean
    RepositoryBuilder repositoryBuilder() throws Exception {
        RepositoryBuilder builder = new RepositoryBuilder();
        builder.setRepositoryDatasourceName(getDatasourcename());
        RepositoryBuilder.setDatabaseType(getDatabasetype());

        Resource resource = new ClassPathResource("mysql.xml");
        File file = resource.getFile();
        String repoFile = file.getAbsolutePath();
        builder.setRepositoryXml(repoFile);
        return builder;
    }
}
