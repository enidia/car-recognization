package com.example.demo.controller;


import com.example.demo.bean.User;
import com.example.demo.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value="/users")     // 通过这里配置使下面的映射都在/users下

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public List<User> getUserList() {
        // 处理"/users/"的GET请求，用来获取用户列表
        // 还可以通过@RequestParam从页面中传递参数来进行查询条件或者翻页信息的传递
        List<User> r = userRepository.findAll();
        return r;
    }

    @ResponseBody
    @RequestMapping(value="/", method=RequestMethod.POST)
    public Map<String, Object> postUser(@ModelAttribute User user) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        // 处理"/users/"的POST请求，用来创建User
        // 除了@ModelAttribute绑定参数之外，还可以通过@RequestParam从页面中传递参数
        try{
            userRepository.save(user);
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value="/{name}", method=RequestMethod.GET)
    public User getUser(@PathVariable String name) {
        // 处理"/users/{name}"的GET请求，用来获取url中id值的User信息
        // url中的id可通过@PathVariable绑定到函数的参数中
        return userRepository.findByUserName(name);
    }

    @ResponseBody
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public Map<String, Object> putUser(@PathVariable String id, @ModelAttribute User user) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 处理"/users/{id}"的PUT请求，用来更新User信息
        try{
            userRepository.save(user);
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value="/{name}", method=RequestMethod.DELETE)
    public Map<String, Object> deleteUser(@PathVariable String name) {
        // 处理"/users/{id}"的DELETE请求，用来删除User
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            userRepository.delete(userRepository.findByUserName(name));
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }
}
