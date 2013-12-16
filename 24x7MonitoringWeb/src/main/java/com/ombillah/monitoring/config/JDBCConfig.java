package com.ombillah.monitoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


//@Component
//@PropertySource(value = { "classpath:config.properties" })
public class JDBCConfig {
	
	
	@Value("${jdbc.dialect}")
	private String jdbcDialect;

	@Value("${jdbc.id.new_generator_mappings}")
	private boolean useNewIdGeneratorMappings;

	@Value("${jdbc.show_sql}")
	private boolean jdbcShowSQL;

	public String getJdbcDialect() {
		return jdbcDialect;
	}

	public void setJdbcDialect(String jdbcDialect) {
		this.jdbcDialect = jdbcDialect;
	}

	public boolean isUseNewIdGeneratorMappings() {
		return useNewIdGeneratorMappings;
	}

	public void setUseNewIdGeneratorMappings(boolean useNewIdGeneratorMappings) {
		this.useNewIdGeneratorMappings = useNewIdGeneratorMappings;
	}

	public boolean isJdbcShowSQL() {
		return jdbcShowSQL;
	}

	public void setJdbcShowSQL(boolean jdbcShowSQL) {
		this.jdbcShowSQL = jdbcShowSQL;
	}

}