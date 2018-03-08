package com.luvai.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.luvai.model.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class MainController {
	private static final Logger logger = LogManager.getLogger(MainController.class);

	@GetMapping("/")
	public String index(ModelMap model) {
		
		String message = "message from ViewController";
		model.addAttribute("message", message);
		return "index";
	}
	
    @MessageMapping("/info")
    @SendTo("/topic/gameInfo")
    public OutputData send(final Data data) throws Exception {
    	logger.info("Received data: <{}> from <{}>", data.getData(), data.getFrom());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputData(data.getFrom(), data.getData(), time);
    }

}
