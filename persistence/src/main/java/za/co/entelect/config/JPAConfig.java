package za.co.entelect.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("za.co.entelect.persistence")
@Import(EnvironmentConfiguration.class)
public class JPAConfig {

    public static final String ENTITIES_PACKAGE = "za.co.entelect.domain";

    @Autowired
    ConfigProperties config;

    @Bean(initMethod = "migrate")
    @Profile("sqlDb")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("/za/co/entelect/migrations");
        flyway.setDataSource(dataSource);
        flyway.setPlaceholderPrefix("~");
        return flyway;
    }

    @Bean(name = "entityManagerFactory")
    @DependsOn("flyway")
    @Profile("sqlDb")
    public LocalContainerEntityManagerFactoryBean serverEntityManagerFactory(DataSource dataSource,
                                                                             JpaVendorAdapter vendorAdapter,
                                                                             LoadTimeWeaver loadTimeWeaver,
                                                                             Properties hibernateProperties) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setLoadTimeWeaver(loadTimeWeaver);
        em.setJpaProperties(hibernateProperties);
        em.setPackagesToScan(ENTITIES_PACKAGE);
        return em;
    }

    @Bean(name = "entityManagerFactory")
    @Profile("h2Db")
    public LocalContainerEntityManagerFactoryBean inMemoryEntityManagerFactory(DataSource dataSource,
                                                                               JpaVendorAdapter vendorAdapter,
                                                                               LoadTimeWeaver loadTimeWeaver,
                                                                               Properties hibernateProperties) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setLoadTimeWeaver(loadTimeWeaver);
        em.setJpaProperties(hibernateProperties);
        em.setPackagesToScan(ENTITIES_PACKAGE);
        return em;
    }

    @Bean
    @Profile("h2Db")
    public JpaVendorAdapter devVendorAdapter(ConfigProperties config) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(config.getJdbcDialect());
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(config.getJdbcShowSql());
        return vendorAdapter;
    }

    @Bean
    @Profile("sqlDb")
    public JpaVendorAdapter deployedVendorAdapter(ConfigProperties config) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(config.getJdbcDialect());
        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(config.getJdbcShowSql());
        return vendorAdapter;
    }

    @Bean(name = "hibernateProperties")
    @Profile("h2Db")
    Properties devHibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create");

        if (config.getJdbcShowSql()) {
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.format_sql", "true");
        }

        properties.setProperty("hibernate.jdbc.batch_size", "30");
        properties.setProperty("hibernate.hbm2ddl.import_files", "dev_initial_data.sql");
        properties.setProperty("hibernate.hbm2ddl.import_files_sql_extractor",
                                "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
        return properties;
    }

    @Bean(name = "h2WebServer", initMethod="start", destroyMethod="stop")
    @Profile("h2DbServer")
    public org.h2.tools.Server h2WebServer() throws SQLException {
        return org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }


    @Bean(initMethod="start", destroyMethod="stop")
    @Profile("h2DbServer")
    @DependsOn(value = "h2WebServer")
    public org.h2.tools.Server h2Server() throws SQLException {
        return org.h2.tools.Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @Bean(name = "hibernateProperties")
    @Profile("sqlDb")
    Properties deployedHibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.jdbc.batch_size", "50");
        if (config.getJdbcShowSql()) {
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.format_sql", "true");
        }

        return properties;
    }

    @Bean
    @Profile("sqlDb")
    public DataSource dataSource(ConfigProperties config) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(config.getJdbcDriverClassName());
        dataSource.setJdbcUrl(config.getJdbcUrl());
        dataSource.setUsername(config.getJdbcUsername());
        dataSource.setPassword(config.getJdbcPassword());
        dataSource.setMinimumIdle(config.getJdbcPoolMinSize());
        dataSource.setMaximumPoolSize(config.getJdbcPoolMaxSize());
        return dataSource;
    }

    @Bean
    @Profile("h2Db")
    public DataSource dataSourceInMemory(ConfigProperties config) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.getJdbcDriverClassName());
        dataSource.setUrl(config.getJdbcUrl());
        dataSource.setUsername(config.getJdbcUsername());
        dataSource.setPassword(config.getJdbcPassword());
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf, DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public LoadTimeWeaver loadTimeWeaver() {
        return new InstrumentationLoadTimeWeaver();
    }
    
    @Bean
    public ProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }
}
