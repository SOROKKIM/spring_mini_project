package com.sparta.spring_hw_memo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/api")
public class HomeController {
    @GetMapping("/home")
    public ModelAndView home() {
        return new ModelAndView("index"); //templates에 있는 index.html 파일 반환해주는 기능
    }
}
