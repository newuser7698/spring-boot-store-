package com.codewithmosh.store.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeConroller {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name ", "User 1");
        return "index";
    }

}
