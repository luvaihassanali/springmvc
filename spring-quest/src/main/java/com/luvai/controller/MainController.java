package com.luvai.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.luvai.model.Data;
import com.luvai.model.OutputData;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class MainController {


	@GetMapping("/")
	public String index(ModelMap model) {
		String message = "message from ViewController";
		model.addAttribute("message", message);
		return "index";
	}
	
    @MessageMapping("/info")
    @SendTo("/topic/gameInfo")
    public OutputData send(final Data data) throws Exception {

        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputData(data.getFrom(), data.getText(), time);
    }

}
