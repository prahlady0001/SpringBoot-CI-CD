package com.spring_boot.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/")
    public String print(){
        return "CI/CD pipeline working and Webhook auto Trigger";
    }
}
