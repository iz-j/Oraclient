package iz.dbui.web.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author iz_j
 *
 */
public class PerformanceLoggingInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingInterceptor.class);

	private static ThreadLocal<StopWatch> swHolder = new ThreadLocal<StopWatch>() {
		@Override
		protected StopWatch initialValue() {
			return new StopWatch();
		}
	};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		swHolder.get().reset();
		swHolder.get().start();
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		swHolder.get().stop();

		String handlerName = "anonymous";
		if (handler instanceof HandlerMethod) {
			final HandlerMethod handlerMethod = (HandlerMethod) handler;
			handlerName = handlerMethod.getBeanType().getName() + "#" + handlerMethod.getMethod().getName();
		}
		if (ex != null) {
			logger.error("Error occured!", ex);
		}

		logger.debug("Response time = {}ms. Handler = {}", swHolder.get().getTime(), handlerName);
	}

}
