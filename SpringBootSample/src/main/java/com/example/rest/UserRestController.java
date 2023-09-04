package com.example.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.user.model.MUser;
import com.example.domain.user.servive.UserService;
import com.example.form.GroupOrder;
import com.example.form.SignupForm;
import com.example.form.UserDetailForm;
import com.example.form.UserListForm;

@RestController
@RequestMapping("/user")
public class UserRestController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping("/get/list")
	public List<MUser> getUserList(UserListForm form) {
		
		MUser user = modelMapper.map(form, MUser.class);
		
		List<MUser> userList = userService.getUsers(user);
		return userList;
	}
	
	@PostMapping("/signup/rest")
	public RestResult postSignup(@Validated(GroupOrder.class) SignupForm form,
			BindingResult bindingResult, Locale locale) {

		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			
			for (FieldError error: bindingResult.getFieldErrors()) {
				String message = messageSource.getMessage(error, locale);
				errors.put(error.getField(), message);
			}
			
			return new RestResult(90, errors);
		}
		
		MUser user = modelMapper.map(form, MUser.class);
		
		userService.signup(user);
		
		return new RestResult(0, null);
	}

	@PutMapping("/update")
	public int updateUser(UserDetailForm form) {
		userService.updateUserOne(
				form.getUserId(),
				form.getPassword(),
				form.getUserName()
		);
		return 0;
	}
	
	@DeleteMapping("/delete")
	public int deleteUser(UserDetailForm form) {
		userService.deleteUserOne(form.getUserId());
		return 0;
	}
}
