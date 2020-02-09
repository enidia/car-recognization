package com.example.demo.controller;

import com.example.demo.bean.HistoryLog;
import com.example.demo.bean.WebSecurityConfig;
import com.example.demo.service.HistoryLogRepository;
import com.example.demo.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController

public class IdentifyController {
    @Autowired
    private HistoryLogRepository historyLogRepository;

    @ResponseBody
    @RequestMapping(value = "/identify", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> imageUpload(@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();

        File convertFile = new File(".\\classes\\static\\"+file.getOriginalFilename());
        convertFile.createNewFile();
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();
        String result = exeCmd("python identify.py "+file.getOriginalFilename(), new File(".\\classes\\static"));

        deleteFile(convertFile);

        File newFile = new File(".\\classes\\static\\precessed_"+file.getOriginalFilename());
        if(result != null && result.length() >= 10) {
            try {
                Thread.sleep(3000);
                if (newFile.exists())
                    System.out.println(newFile.getPath());
                else System.out.println("no file");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        Long second = localDateTime.toEpochSecond(ZoneOffset.of("+8"));

        if(result != null && result.length() >= 10){
            result = result.substring(2,9);
            map.put("success",true);
            map.put("result", result);
            map.put("identityTime", second);
            String userName = session.getAttribute(WebSecurityConfig.SESSION_KEY).toString();
            if(userName != null){
                HistoryLog historyLog = new HistoryLog(userName,file.getOriginalFilename(),result,localDateTime);
                historyLogRepository.save(historyLog);
            }else{
                map.put("success",false);
                map.put("result", null);
                map.put("identityTime", null);
            }
        }else{
            map.put("success",false);
            map.put("result", null);
            map.put("identityTime", null);
        }
        return map;
    }

    public String exeCmd(String commandStr, File dir) {
        BufferedReader br = null;
        String result = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr,null, dir);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                result = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    public boolean deleteFile(File file)
    {
        try{
            if(file.delete()){
                System.out.println(file.getName() + " 文件已被删除！");
                return true;
            }else{
                System.out.println("文件删除失败！");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
