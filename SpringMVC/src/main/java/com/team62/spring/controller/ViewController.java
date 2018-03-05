package com.team62.spring.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.team62.spring.model.CardList;

@Controller
public class ViewController {

   @GetMapping("/")
   public String index(ModelMap model) {
		String message =  "message from ViewController";
		model.addAttribute("message", message);
		return "index";
   }
   
   @GetMapping("/view2")
   public String view2() {
      return "view2";
   }
   
   @GetMapping("/welcome")
	public ModelAndView helloWorld() {
	   
		String message = CardList.amourImage;
		return new ModelAndView("welcome", "message", message);
	}
  	   
}