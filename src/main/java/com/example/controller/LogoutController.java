package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * Springセキュリティを導入していればこのクラスは不要
 * @author user
 *
 */
@Controller
@Slf4j
public class LogoutController {

	@PostMapping("/logout")
	public String postLogout() {
		log.info("ログアウト");
		return "redirect:/login";
	}
}
