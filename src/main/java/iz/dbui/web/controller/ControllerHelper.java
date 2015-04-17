package iz.dbui.web.controller;

import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author izumi_j
 *
 */
public class ControllerHelper {
	private ControllerHelper() {
	}

	public static ModelAndView createModelAndViewForPage(String pathToTemplate, String pageTitle) {
		final ModelAndView mv = new ModelAndView(pathToTemplate);
		mv.addObject("_pageTitle", pageTitle);
		return mv;
	}
}
