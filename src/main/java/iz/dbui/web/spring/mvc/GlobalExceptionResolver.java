package iz.dbui.web.spring.mvc;

import iz.dbui.web.spring.jdbc.DatabaseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class GlobalExceptionResolver implements HandlerExceptionResolver {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		logger.error("Request failed!", ex);
		final ModelAndView mv = new ModelAndView();
		if (ex instanceof DatabaseException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			mv.setView(new MappingJackson2JsonView());
			final DatabaseException dbEx = (DatabaseException) ex;
			mv.addObject("message", dbEx.getSqlExceptionMessage());
			mv.addObject("rowid", dbEx.getRowid());
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return mv;
	}

}
