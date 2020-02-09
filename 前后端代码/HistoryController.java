package com.example.demo.controller;

import com.example.demo.bean.HistoryLog;
import com.example.demo.bean.User;
import com.example.demo.service.HistoryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value="/history")     // 通过这里配置使下面的映射都在/history

public class HistoryController {
    @Autowired
    private HistoryLogRepository historyLogRepository;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public List<HistoryLog> getHistoryList() {
        // 处理"/history/"的GET请求，用来获取用户列表
        // 还可以通过@RequestParam从页面中传递参数来进行查询条件或者翻页信息的传递
        List<HistoryLog> r = historyLogRepository.findAll();
        return r;
    }

    @ResponseBody
    @RequestMapping(value="/", method=RequestMethod.POST)
    public Map<String, Object> postHistoryLog(@ModelAttribute HistoryLog historyLog) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        // 处理"/history/"的POST请求，用来创建HistoryLog
        // 除了@ModelAttribute绑定参数之外，还可以通过@RequestParam从页面中传递参数
        try{
            historyLogRepository.save(historyLog);
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value="/{name}", method=RequestMethod.GET)
    public List<HistoryLog> getHistoryLog(@PathVariable String name) {
        // 处理"/history/{name}"的GET请求，用来获取url中id值的User信息
        // url中的id可通过@PathVariable绑定到函数的参数中
        List<HistoryLog> r = historyLogRepository.findAll();
        ArrayList<HistoryLog> result = new ArrayList<HistoryLog>();
        for (HistoryLog historyLog:r
             ) {
            String a = historyLog.getUserName();
            if(a.equals(name))
                result.add(historyLog);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/{name}", method=RequestMethod.PUT)
    public Map<String, Object> putHistoryLog(@PathVariable String name, @ModelAttribute HistoryLog historyLog) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 处理"/historyLog/{name}"的PUT请求，用来更新historyLog信息
        try{
            historyLogRepository.save(historyLog);
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public Map<String, Object> deleteHistoryLogByName(@PathVariable int id) {
        // 处理"/historyLog/{name}"的DELETE请求，用来删除historyLog
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            List<HistoryLog> historylist = historyLogRepository.findAll();
            for (HistoryLog historyLog:historylist
                 ) {
                if(historyLog.getLogId() == id)
                historyLogRepository.delete(historyLog);
            }
            map.put("success", true);
        } catch (Exception e){
            map.put("success", false);
        }
        return map;
    }
}
