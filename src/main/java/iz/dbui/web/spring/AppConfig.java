package iz.dbui.web.spring;

import iz.dbui.web.spring.jdbc.DataSourceRouter;
import iz.dbui.web.spring.jdbc.TxManagerWrapper;

import javax.sql.DataSource;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author iz_j
 *
 */
@Configuration
@EnableTransactionManagement
@EnableCaching
@ComponentScan(basePackages = { "iz.dbui.web" })
public class AppConfig {

	@Bean
	public DataSource dataSource() {
		return new DataSourceRouter();
	}

	@Bean
	public PlatformTransactionManager txManager() {
		final TxManagerWrapper txManager = new TxManagerWrapper();
		txManager.setDataSource(dataSource());
		return txManager;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cmfb.setShared(true);
		return cmfb;
	}
}
