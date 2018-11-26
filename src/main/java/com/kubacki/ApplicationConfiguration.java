package com.kubacki;

import com.kubacki.domain.TastingService;
import com.kubacki.domain.repo.TastingRepo;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kubacki")
@PropertySources({ @PropertySource("classpath:application-cfg.properties") })
public class ApplicationConfiguration {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getProperty("db.database-driver"));
        System.out.println("## Database URL IS : " + env.getProperty("db.url"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        System.out.println("## getDataSource: " + dataSource);
        return dataSource;
    }

    @Bean(name = "tastingService")
    public TastingService getTastingService() {
        System.out.println("## tastingService created");
        return new TastingService();
    }

    @Bean(name = "tastingRepo")
    public TastingRepo getTastingRepo() {
        System.out.println("## tastingRepo created");
        return new TastingRepo(getDataSource());
    }
}