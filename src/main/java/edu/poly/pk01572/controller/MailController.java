package edu.poly.pk01572.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("send-mail")
public class MailController {
	@Autowired
	JavaMailSender mailSender;

//	@RequestParam("to") String to, @RequestParam("subject") String subject,
//	@RequestParam("conent") String conent

	@GetMapping("/send")
	public String sendMail(String to, String subject, String message) {

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(message);

		mailSender.send(msg);

		return "redirect:/home/products";
	}
}
