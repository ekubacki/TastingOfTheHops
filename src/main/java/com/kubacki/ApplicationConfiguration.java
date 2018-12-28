package com.kubacki;

import com.kubacki.domain.TastingService;
import com.kubacki.domain.repo.TastingRepo;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import org.apache.log4j.Logger;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.kubacki")
@PropertySources({ @PropertySource("classpath:application-cfg.properties") })
public class ApplicationConfiguration {

    @Autowired
    private Environment env;

    private static final Logger log = Logger.getLogger(ApplicationConfiguration.class);

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getProperty("db.database-driver"));
        log.info("## Database URL IS : " + env.getProperty("db.url"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        log.info("## getDataSource: " + dataSource);
        return dataSource;
    }

    @Bean(name = "tastingService")
    public TastingService getTastingService() {
        log.info("## tastingService created");
        return new TastingService();
    }

    @Bean(name = "tastingRepo")
    public TastingRepo getTastingRepo() {
        log.info("## tastingRepo created");
        return new TastingRepo(getDataSource());
    }
}