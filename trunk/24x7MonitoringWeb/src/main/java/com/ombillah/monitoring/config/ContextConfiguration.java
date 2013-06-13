package com.ombillah.monitoring.config;


import java.net.URL;

import net.sf.ehcache.CacheManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
 
/**
 * Application Context java Config
 * @author Oussama M Billah
 *
 */

@Configuration
@ComponentScan(basePackages = {"com.ombillah.monitoring"})
public class ContextConfiguration {
 
    @Bean
    public CacheManager cacheManager() {
    	URL url = getClass().getResource("/ehcache.xml");
    	CacheManager cacheManager = CacheManager.create(url);
    	return cacheManager;
    }
 
}