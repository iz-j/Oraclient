package iz.dbui.web.spring;

import iz.dbui.web.spring.mvc.GlobalExceptionResolver;
import iz.dbui.web.spring.mvc.PerformanceLoggingInterceptor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * Web mvc configration.<br>
 * <ul>
 * <li>Configure directories for several resources.
 * <li>Enable thymeleaf as template engine.
 * </ul>
 * and so on.
 *
 * @author izumi_j
 *
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurationSupport {

	private static final String TEMPLATE = "RES-WEB/template/";
	private static final String[] RESOURCES_HANDLERS = new String[] { "/js/", "/css/", "/img/", "/lib/" };

	@Override
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
		requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
		requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
		requestMappingHandlerMapping.setInterceptors(new Object[] { performanceLoggingInterceptor() });
		return requestMappingHandlerMapping;
	}

	@Override
	@Bean
	public HandlerMapping resourceHandlerMapping() {
		return super.resourceHandlerMapping();
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		for (String resourceHandler : RESOURCES_HANDLERS) {
			registry.addResourceHandler(resourceHandler + "**").addResourceLocations(resourceHandler);
		}
	}

	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public TemplateResolver templateResolver() {
		TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix(TEMPLATE);
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		thymeleafViewResolver.setOrder(1);
		thymeleafViewResolver.setViewNames(new String[] { "*" });
		thymeleafViewResolver.setCache(false);
		return thymeleafViewResolver;
	}

	@Bean
	public PerformanceLoggingInterceptor performanceLoggingInterceptor() {
		return new PerformanceLoggingInterceptor();
	}

	@Override
	@Bean
	public HandlerExceptionResolver handlerExceptionResolver() {
		return super.handlerExceptionResolver();
	}

	@Override
	protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new GlobalExceptionResolver());
	}
}
