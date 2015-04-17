package iz.dbui.web.controller;

import iz.dbui.web.process.database.DatabaseService;
import iz.dbui.web.process.database.dto.ExecutionResult;
import iz.dbui.web.process.database.dto.SqlTemplate;
import iz.dbui.web.spring.jdbc.ConnectionDeterminer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/processor")
public class ProcessorController {
	private static final Logger logger = LoggerFactory.getLogger(ProcessorController.class);

	@Autowired
	private DatabaseService dbService;

	@RequestMapping(value = "/{connectionId}", method = RequestMethod.GET)
	public ModelAndView page(@PathVariable("connectionId") String connectionId) {
		logger.trace("#page connectionId = {}", connectionId);
		final ModelAndView mv = new ModelAndView("workspace/processor");
		mv.addObject("connectionId", connectionId);
		return mv;
	}

	@RequestMapping(value = "/execute/{connectionId}", method = RequestMethod.POST)
	public @ResponseBody ExecutionResult execute(@PathVariable("connectionId") String connectionId,
			@RequestBody SqlTemplate sql) {
		logger.trace("#execute {}", sql);
		ConnectionDeterminer.setId(connectionId);
		return dbService.executeSql(sql);
	}
}
