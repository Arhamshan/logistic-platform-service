package com.logistic.platform.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceConfig {

	public Properties connectionPoolProperties();

	public DataSourceProperties dataSourceProperties();

	public DataSource dataSource(DataSourceProperties dsProperties, Properties cpProperties);

	public JdbcTemplate jdbcTemplate(DataSource dataSource);

	public PlatformTransactionManager platformTransactionManager(DataSource dataSource);

}
