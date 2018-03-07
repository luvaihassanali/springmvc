package com.luvai.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.luvai.model.Message;
import com.luvai.model.OutputMessage;
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
    public OutputMessage send(final Message message) throws Exception {

        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }

}
