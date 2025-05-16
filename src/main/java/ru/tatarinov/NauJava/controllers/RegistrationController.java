package ru.tatarinov.NauJava.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.tatarinov.NauJava.entity.Member;
import ru.tatarinov.NauJava.entity.dto.MemberRegistrationDto;
import ru.tatarinov.NauJava.repository.MemberRepository;
import ru.tatarinov.NauJava.service.MemberService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final MemberService memberService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("member", new MemberRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerMember(@Valid MemberRegistrationDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }

        if (memberService.findByUsername(dto.getUsername()).isPresent()) {
            result.rejectValue("username", "error.member", "Username already exists");
            return "register";
        }

        memberService.registerNewMember(dto);
        return "redirect:/login?registered";
    }
}
