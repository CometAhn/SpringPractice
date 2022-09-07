package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test1")
public class TestWebController {
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}