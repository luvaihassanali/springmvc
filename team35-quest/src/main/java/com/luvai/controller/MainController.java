package com.luvai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public String index(ModelMap model) {
		String message = "Networking version";
		model.addAttribute("message", message);
		return "index";
	}

}
