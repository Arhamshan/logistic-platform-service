package com.logistic.platform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class WriterDataSourceConfig implements DataSourceConfig{

	@Bean(name = "writerConnectionPoolProperties")
	@ConfigurationProperties("spring.datasource.writer.cp")
	@Override
	public Properties connectionPoolProperties() {
		return new Properties();
	}

	@Primary
	@Bean(name = "writerConfigProperties")
	@ConfigurationProperties("spring.datasource.writer.config")
	@Override
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}


	@Primary
	@Bean(name = "writerDataSource")
	@Override
	public DataSource dataSource(@Qualifier("writerConfigProperties") DataSourceProperties dsProperties,
								 @Qualifier("writerConnectionPoolProperties") Properties cpProperties) {

		HikariDataSource ds = dsProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		ds.setMaximumPoolSize(Integer.valueOf(cpProperties.getProperty("hikari.maximumPoolSize")));
		ds.setIdleTimeout(Long.valueOf(cpProperties.getProperty("hikari.idleTimeout")));
		ds.setMinimumIdle(Integer.valueOf(cpProperties.getProperty("hikari.minimumIdle")));
		ds.setPoolName(cpProperties.getProperty("hikari.poolName"));
		ds.setConnectionTimeout(Long.valueOf(cpProperties.getProperty("hikari.connectionTimeout")));

		return ds;
	}

	@Primary
	@Bean(name = "writer")
	@Override
	public JdbcTemplate jdbcTemplate(@Qualifier("writerDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Primary
	@Bean(name = "writerTx")
	@Override
	public PlatformTransactionManager platformTransactionManager(@Qualifier("writerDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

}
