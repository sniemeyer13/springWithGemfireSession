package com.example.client;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class DemoController {

    @RequestMapping("/")
    @ResponseBody
    public Map root(HttpServletRequest request) {
        HttpSession session = request.getSession();

        updateFoo(session);

        return presentFoo((Integer) session.getAttribute("foo"));
    }

    private Map<String, Object> presentFoo(Integer foo) {
        Map<String, Object> result = new HashMap<>();
        result.put("foo", foo);
        return result;
    }

    private HttpSession updateFoo(HttpSession session) {
        Integer foo = updateFoo((Integer) session.getAttribute("foo"));
        session.setAttribute("foo", foo);
        return session;
    }

    private Integer updateFoo(Integer oldFoo) {
        return (oldFoo == null ? 1 : ++oldFoo);
    }

    @RequestMapping("/attributes")
    @ResponseBody
    public Map attributes(HttpSession session,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "value", required = false) String value) {

        if (isSet(name) && isSet(value)) {
            session.setAttribute(name, value);
        }

        updateFoo(session);

        return attributes(session);
    }

    private Map<String, String> attributes(HttpSession session) {
        Map<String, String> attributes = new HashMap<>();

        for (String attributeName : toIterable(session.getAttributeNames())) {
            attributes.put(attributeName, String.valueOf(session.getAttribute(attributeName)));
        }

        return attributes;
    }

    private boolean isSet(String value) {
        return StringUtils.hasText(value);
    }

    private <T> Iterable<T> toIterable(Enumeration<T> enumeration) {
        return () -> new Iterator<T>() {
            @Override public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override public T next() {
                return enumeration.nextElement();
            }
        };
    }
}
