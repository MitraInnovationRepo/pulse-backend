package com.pulse.mst.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class IndexController {

    @RequestMapping({"/pulse-dashboard","/login","/","/offers","/team","/about"})
    public String defaultPage() {
        return "index";
    }


}
