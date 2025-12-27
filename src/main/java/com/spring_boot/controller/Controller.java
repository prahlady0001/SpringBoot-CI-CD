package com.spring_boot.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/api/version")
    public String version(){
        return "VERSION = NEXT-DEPLOY";
    }
}
