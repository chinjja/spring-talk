package com.chinjja.talk.domain.auth.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.chinjja.talk.domain.auth.dto.ResetPasswordRequest;
import com.chinjja.talk.domain.auth.services.ResetPasswordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SessionAttributes({"uuid"})
public class ResetPasswordController {
	private final ResetPasswordService resetPasswordService;
	
	@PostMapping("/reset/{email}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void sendResetEmail(@PathVariable("email") String email) {
		log.info("send reset password email {}", email);
		resetPasswordService.sendEmail(email);
	}
	
	@GetMapping("/reset/{uuid}")
	public String getResetPasswordView(
			Model model,
			@PathVariable("uuid") String uuid) {
		log.info("reset password form. uuid: {}", uuid);
		if(resetPasswordService.isValid(uuid)) {
			model.addAttribute("uuid", uuid);
			model.addAttribute("resetPassword", new ResetPasswordRequest());
			return "reset-password/form";
		} else {
			return "reset-password/not-found";
		}
	}
	
	@PostMapping("/reset")
	public String resetPassword(
			@ModelAttribute("uuid") String uuid,
			@ModelAttribute("resetPassword") @Valid ResetPasswordRequest request) {
		log.info("reset password. uuid: {}, password: {}", uuid, request);
		try {
			resetPasswordService.reset(uuid, request.getPassword());
			return "reset-password/done";
		} catch(Exception e) {
			return "/redirect:/auth/reset/"+uuid;
		}
	}
	
	@ExceptionHandler(BindException.class)
	public String handler(BindException e, Model model) {
		model.addAttribute("reason", e.getMessage());
		return "reset-password/bad-password";
	}
}
