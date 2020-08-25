package com.pankaj.web;

import com.pankaj.web.forms.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

/**
 * GET http://localhost:8080/?username=pankaj
 *
 * Hello pankaj, this is the current date 02-07-2020 15:10
 *
 * GET http://localhost:8080/?username=
 *
 * Hello stranger, this is the current date 02-07-2020 15:12
 * GET http://localhost:8080/?username=zarco
 */
@Controller
public class WebsiteController {
    @GetMapping("/")
    public String homepage(Model model,
                           @RequestParam(required = false,defaultValue = "stranger")String username){
        model.addAttribute("username",username);
        //plain Thymeleaf does not work with Java 8+ (LocalDate etc.) dates.
        // You would need to add another library called thymeleaf-extras-java8time
        model.addAttribute("currentDate",new Date());
        return "index.html";
    }

    @GetMapping("/login")
    public String login(Model model){

        model.addAttribute("loginForm",new LoginForm());
        return "login.html";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute @Valid LoginForm loginForm, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "login.html";
        }

        if(loginForm.getUsername().equals(loginForm.getPassword())){
            return "redirect:/";
        }
        model.addAttribute("invalidCredentials","true");
        return "login.html";
    }
}
