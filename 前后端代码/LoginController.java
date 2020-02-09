package com.example.demo.controller;

import com.example.demo.bean.User;
import com.example.demo.bean.WebSecurityConfig;
import com.example.demo.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller

public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY)String account, Model model){
        return "index";
    }

    @GetMapping("/loginMain")
    public String loginmain() {
        return "loginMain";
    }
    @GetMapping("/Main.html**")
    public String windowmain() {
        return "Main";
    }
    @GetMapping("/modify.html**")
    public String nodifymain() {
        return "modify";
    }
    @GetMapping("/register.html**")
    public String registermain() {
        return "register";
    }
    @GetMapping("/history.html**")
    public String history() {
        return "history";
    }


    @ResponseBody
    @RequestMapping(value="/login", method= RequestMethod.POST)
    public Map<String, Object> login(@RequestParam String userName, @RequestParam String password, HttpSession session) {

        Map<String, Object> map = new HashMap<>();

        User user = userRepository.findByUserName(userName);
        if(user != null){
            if (user.getPassword().equals(password)) {
                // 设置session
                session.setAttribute(WebSecurityConfig.SESSION_KEY, userName);
                map.put("success", true);
                map.put("message", "login success");
            } else {
                map.put("success", false);
                map.put("message", "login fail");
            }
        }
        else {
            map.put("success", false);
            map.put("message", "login fail");
        }
        return map;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 移除session
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/loginMain";
    }

    @ResponseBody
    @RequestMapping(value="/register", method= RequestMethod.POST)
    public Map<String, Object> register(@RequestParam String userName, @RequestParam String password) {
        Map<String, Object> map = new HashMap<>();
        User user = userRepository.findByUserName(userName);
        if(user == null){
            user = new User();
            user.setUserName(userName);
            user.setPassword(password);
            user.setUserRank(0);
            userRepository.save(user);
            map.put("success", true);
            map.put("message", "register success");
        }else{
            map.put("success", false);
            map.put("message", "user exist");
        }
        return map;
    }
}
