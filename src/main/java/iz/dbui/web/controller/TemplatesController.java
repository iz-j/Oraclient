package iz.dbui.web.controller;

import iz.dbui.web.process.users.UserDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/templates")
public class TemplatesController {
	private static final Logger logger = LoggerFactory.getLogger(TemplatesController.class);

	@Autowired
	private UserDataService service;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView page() {
		logger.trace("#page");
		final ModelAndView mv = ControllerHelper.createModelAndViewForPage("templates/main", "Templates");
		return mv;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView getTemplates() {
		logger.trace("#getTemplates");
		final ModelAndView mv = new ModelAndView("templates/list");
		mv.addObject("templates", service.getSqlTemplates());
		return mv;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView removeTemplate(@RequestParam("id") String id) {
		logger.trace("#removeTemplate id = {}", id);
		service.removeSqlTemplate(id);
		return getTemplates();
	}
}
