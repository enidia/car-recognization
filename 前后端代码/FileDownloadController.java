package com.example.demo.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController

public class FileDownloadController {
    @RequestMapping(value = "/download/{name}", method = RequestMethod.GET)
    public ResponseEntity<Object> downloadFile(@PathVariable("name") String name) throws IOException{
        String filename = ".\\classes\\static\\processed_"+name;
        System.out.println(filename);
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("file not exist");
        }
        ResponseEntity<Object> responseEntity;
        try{
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");


            ResponseEntity.BodyBuilder ok = ResponseEntity.ok();
            ok.headers(headers);
            ok.contentLength(file.length());
            ok.contentType(MediaType.parseMediaType("application/txt"));
            responseEntity = ok.body(resource);

            //file.delete();
        }catch (Exception e){
            System.err.println(e);
            return new ResponseEntity<Object>("File does not exist", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
