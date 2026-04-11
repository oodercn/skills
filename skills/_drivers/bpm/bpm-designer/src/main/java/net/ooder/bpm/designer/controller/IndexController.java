package net.ooder.bpm.designer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"/", "/designer", "/designer/"})
    public String index() {
        return "forward:/static/designer/index.html";
    }
}
