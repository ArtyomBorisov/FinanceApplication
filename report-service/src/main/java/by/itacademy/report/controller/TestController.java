package by.itacademy.report.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class TestController {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        return "report";
    }
}
