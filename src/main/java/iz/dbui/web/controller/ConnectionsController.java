package iz.dbui.web.controller;

import iz.dbui.web.process.connection.ConnectionService;
import iz.dbui.web.process.connection.dto.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/connections")
public class ConnectionsController {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionsController.class);

	@Autowired
	private ConnectionService service;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView page() {
		logger.trace("#page");
		final ModelAndView mv = ControllerHelper.createModelAndViewForPage("connections/main", "Connections");
		mv.addObject("connections", service.getAll());
		return mv;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView getConnections() {
		logger.trace("#getConnections");
		final ModelAndView mv = new ModelAndView("connections/list");
		mv.addObject("connections", service.getAll());
		return mv;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ModelAndView addNewConnection(@RequestBody Connection connection) {
		logger.trace("#addNewConnection {}", connection);
		service.add(connection);
		return getConnections();
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public ModelAndView removeConnection(@RequestParam("id") String id) {
		logger.trace("#removeConnection id = {}", id);
		service.remove(id);
		return getConnections();
	}
}
