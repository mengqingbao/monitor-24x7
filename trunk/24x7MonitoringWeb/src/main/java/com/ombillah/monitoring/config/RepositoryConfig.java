package com.ombillah.monitoring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.apache.commons.dbcp.BasicDataSource;

@Configuration
@EnableTransactionManagement
public class RepositoryConfig {

	@Bean
	@Autowired
	public BasicDataSource getDataSource(DatabaseConfig config) {
		BasicDataSource datasource = new BasicDataSource();
		datasource.setDriverClassName(config.getDriverClass());
		datasource.setUrl(config.getConnectionURL());
		datasource.setUsername(config.getUserID());
		datasource.setPassword(config.getPassword());
		datasource.setValidationQuery("SELECT 1");
		return datasource;
	}


	@Bean
	@Autowired
	public DataSourceTransactionManager  getTransactionManager(
			BasicDataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	/**
	 * inclusion The PropertySourcesPlaceholderConfigurer automatically lets the
	 * annotation included property files to be scanned. setting it static to
	 * spawn on startup.
	 * 
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer ph = new PropertySourcesPlaceholderConfigurer();
		ph.setIgnoreUnresolvablePlaceholders(true);
		return ph;
	}
}