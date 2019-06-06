package com.ylz.jckdemo.web;

import com.ylz.jckdemo.service.DbRepositoryService;
import com.ylz.jckdemo.service.FileRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "jcr")
public class JckController {

    @Autowired
    FileRepositoryService service;

    @Autowired
    DbRepositoryService dbRepositoryService;

    @RequestMapping(value = "testData")
    String test() throws Exception {
        //service.createNodes();
        String test = service.findData();
        System.out.println(test);
        return "Hello";
    }

    @RequestMapping(value = "testFile")
    String testFile() throws Exception {
        //service.createNodes();
        service.saveFile();
        return "Hello";
    }

    @RequestMapping(value = "readFile")
    String readFile() throws Exception {
        //service.createNodes();
        service.readFileFromContent();
        return "Hello";
    }
}
