package com.example;

import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {
    @RequestMapping("/")
    @ResponseBody
    public Map root(HttpServletRequest request) {
        request.getSession(true).setAttribute("createdAt", OffsetDateTime.now());
        HashMap<String, Object> result = new HashMap<>();
        result.put("foo", 1);
        return result;
    }
}
