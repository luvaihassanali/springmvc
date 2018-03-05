package com.team62.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

   @GetMapping("/")
   public String index() {
      return "index";
   }
   
   @GetMapping("/view2")
   public String view2() {
      return "view2";
   }

}
