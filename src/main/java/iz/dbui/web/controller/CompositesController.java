package iz.dbui.web.controller;

import iz.dbui.web.process.database.DatabaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/composites")
public class CompositesController {
	private static final Logger logger = LoggerFactory.getLogger(CompositesController.class);

	@Autowired
	private DatabaseService service;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView page() {
		logger.trace("#page");
		final ModelAndView mv = ControllerHelper.createModelAndViewForPage("composites/main", "Composites");
		return mv;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView getComposites() {
		logger.trace("#getComposites");
		final ModelAndView mv = new ModelAndView("composites/list");
		mv.addObject("composites", service.getAllSqlComposite());
		return mv;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView removeComposite(@RequestParam("id") String id) {
		logger.trace("#removeComposite id = {}", id);
		service.removeSqlComposite(id);
		return getComposites();
	}

}
