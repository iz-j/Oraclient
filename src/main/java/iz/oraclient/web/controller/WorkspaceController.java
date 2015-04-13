package iz.oraclient.web.controller;

import iz.oraclient.web.process.connection.ConnectionService;
import iz.oraclient.web.process.connection.dto.Connection;
import iz.oraclient.web.process.database.DatabaseService;
import iz.oraclient.web.process.database.dto.SqlTemplate;
import iz.oraclient.web.spring.jdbc.ConnectionDeterminer;
import iz.oraclient.web.spring.jdbc.DatabaseException;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/workspace")
public class WorkspaceController {
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

	@Autowired
	private ConnectionService connectionService;

	@Autowired
	private DatabaseService sqlService;

	@RequestMapping(value = "/{connectionId}", method = RequestMethod.GET)
	public ModelAndView page(@PathVariable("connectionId") String connectionId) {
		logger.trace("#page connectionId = {}", connectionId);
		final Connection c = connectionService.get(connectionId);
		ModelAndView mv;
		try {
			connectionService.activateConnection(connectionId);
			mv = ControllerHelper.createModelAndViewForPage("workspace/main", c.name);
			mv.addObject("connectionId", connectionId);
		} catch (DatabaseException e) {
			mv = ControllerHelper.createModelAndViewForPage("workspace/connect-error", c.name);
			mv.addObject("errorMessage", e.getMessage());
			mv.addObject("errorDetail", e.getStackTraceAsString());
		}
		return mv;
	}

	@RequestMapping(value = "/sqlTemplates", method = RequestMethod.GET)
	public @ResponseBody List<SqlTemplate> findSqltemplates(@RequestParam("connectionId") String connectionId,
			@RequestParam("term") String term) {
		logger.trace("#findSqltemplates term = {}", term);
		ConnectionDeterminer.setId(connectionId);
		return sqlService.getMatchedTemplates(term);
	}

	@RequestMapping(value = "/sqlItemView", method = RequestMethod.POST)
	public ModelAndView sqlItemView(@RequestBody SqlTemplate sqlTemplate) {
		logger.trace("#sqlItemView sqlTemplate = {}", sqlTemplate);
		final ModelAndView mv = new ModelAndView("workspace/sql-item");
		mv.addObject("sqls", Arrays.asList(sqlTemplate));
		return mv;
	}

}
