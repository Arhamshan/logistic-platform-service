package com.logistic.platform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class ReaderDataSourceConfig implements DataSourceConfig {

	@Bean(name = "readerConnectionPoolProperties")
	@ConfigurationProperties("spring.datasource.reader.cp")
	@Override
	public Properties connectionPoolProperties() {
		return new Properties();
	}

	@Bean(name = "readerConfigProperties")
	@ConfigurationProperties("spring.datasource.reader.config")
	@Override
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "readerDataSource")
	@Override
	public DataSource dataSource(@Qualifier("readerConfigProperties") DataSourceProperties dsProperties,
								 @Qualifier("readerConnectionPoolProperties") Properties cpProperties) {

		HikariDataSource ds = dsProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		ds.setMaximumPoolSize(Integer.valueOf(cpProperties.getProperty("hikari.maximumPoolSize")));
		ds.setIdleTimeout(Long.valueOf(cpProperties.getProperty("hikari.idleTimeout")));
		ds.setMinimumIdle(Integer.valueOf(cpProperties.getProperty("hikari.minimumIdle")));
		ds.setPoolName(cpProperties.getProperty("hikari.poolName"));
		ds.setConnectionTimeout(Long.valueOf(cpProperties.getProperty("hikari.connectionTimeout")));

		return ds;
	}

	@Bean(name = "reader")
	@Override
	public JdbcTemplate jdbcTemplate(@Qualifier("readerDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(name = "readerTx")
	@Override
	public PlatformTransactionManager platformTransactionManager(@Qualifier("readerDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}


}
