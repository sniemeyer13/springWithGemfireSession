package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {

    private final TestGemfire tester;

    @Autowired
    public DemoController(TestGemfire tester) {
        this.tester = tester;
    }

    @RequestMapping("/")
    @ResponseBody
    public Map root(HttpServletRequest request) {
        Session session = tester.interactWithGemfire();

        Integer foo =  updatedFoo((Integer) session.getAttribute("foo"));
        session.setAttribute("foo", foo);

        return presentFoo(foo);
    }

    @RequestMapping("/sdg")
    @ResponseBody
    public Map sdgExample(HttpServletRequest request) {
        HttpSession session = request.getSession();

        Integer foo = updatedFoo((Integer) session.getAttribute("foo"));
        session.setAttribute("foo", foo);

        return presentFoo(foo);
    }

    private Integer updatedFoo(Integer oldFoo) {
        if (oldFoo == null) {
            oldFoo = 1;
        } else {
            oldFoo++;
        }
        return oldFoo;
    }

    private HashMap<String, Object> presentFoo(Integer foo) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foo", foo);
        return result;
    }
}
