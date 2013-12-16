package com.ombillah.monitoring.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class JDBCProperties extends Properties {

	private static final long serialVersionUID = 1L;

	@Autowired
	public JDBCProperties(JDBCConfig config) {
		setProperty("jdbc.dialect", config.getJdbcDialect());
		setProperty("jdbc.id.new_generator_mappings", config.isUseNewIdGeneratorMappings() ? "true" : "false");
		setProperty("jdbc.show_sql", config.isJdbcShowSQL() ? "true" : "false");
	}
}