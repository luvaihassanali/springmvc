package com.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
 

@Controller
public class IndexController {
 
	@RequestMapping("/welcome")
	public ModelAndView showIndex() {
 
		String message = "THIS MESSAGE IS FROM THE CONTROLLER, THIS IS WELCOME PAGE";
		return new ModelAndView("welcome", "message", message);
	}
	
	@RequestMapping("/anotherView")
	public ModelAndView showAnotherView() {
 
		String message = "THIS MESSAGE IS FROM THE CONTROLLER, THIS IS 'ANOTHERVIEW' PAGE";
		return new ModelAndView("anotherView", "message", message);
	}
}