package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.user.model.MUser;
import com.example.domain.user.service.UserService;

@Controller
@RequestMapping("/user")
public class UserListController {

	@Autowired
	private UserService userService;

	@GetMapping("/list")
	public String getUserList(Model model) {

		// ユーザー一覧取得
		List<MUser> userList = userService.getUsers();

		// Modelに登録
		model.addAttribute("userList", userList);

		return "user/list";
	}
}
