package iz.dbui.web.controller;

import iz.dbui.web.process.ProcessConstants;
import iz.dbui.web.process.connection.ConnectionService;
import iz.dbui.web.process.connection.dto.Connection;
import iz.dbui.web.process.database.DatabaseService;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.process.database.helper.SqlFormatter;
import iz.dbui.web.spring.jdbc.ConnectionContext;
import iz.dbui.web.spring.jdbc.DatabaseException;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/workspace")
public class WorkspaceController {
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

	@Autowired
	private ConnectionService connectionService;

	@Autowired
	private DatabaseService dbService;

	@Autowired
	private CacheManager cacheManager;

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
		ConnectionContext.setId(connectionId);
		return dbService.getMatchedTemplates(term);
	}

	@RequestMapping(value = "/sqlItemView", method = RequestMethod.POST)
	public ModelAndView sqlItemView(@RequestBody(required = false) SqlTemplate sqlTemplate) {
		logger.trace("#sqlItemView sqlTemplate = {}", sqlTemplate);
		final ModelAndView mv = new ModelAndView("workspace/sql-item");
		if (sqlTemplate == null) {
			sqlTemplate = SqlTemplate.forFreeSql();
		}
		mv.addObject("sqls", Arrays.asList(sqlTemplate));
		return mv;
	}

	@RequestMapping(value = "/formatSql", method = RequestMethod.POST)
	public @ResponseBody String formatSql(@RequestBody SqlTemplate sql) {
		logger.trace("#formatSql sql = {}", sql);
		return SqlFormatter.format(sql.sentence);
	}

	@RequestMapping(value = "/saveTemplate", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void saveTemplate(@RequestBody SqlTemplate sqlTemplate) {
		logger.trace("#saveTemplate");
		dbService.save(sqlTemplate);
	}

	@RequestMapping(value = "/clearCache", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void clearCache() {
		logger.trace("#clearCache");
		cacheManager.getCache(ProcessConstants.CACHE_DATABASE).clear();
	}

	@RequestMapping(value = "/sqlCompletions", method = RequestMethod.GET)
	public @ResponseBody List<String> sqlCompletions(@RequestParam("term") String term) {
		logger.trace("#sqlCompletions");
		return dbService.getSqlCompletions(term);
	}
}
