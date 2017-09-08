package com.dyrs.api.controller;

import com.dyrs.api.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: Wang Anbang
 * Time: 17-9-7 下午5:42
 */
@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private SystemService systemService;


    @RequestMapping(
            value = "/init",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Map<String, Object>> init(){
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;
        try {
            systemService.init();
            map.put("result", true);
            map.put("msg", "Success");
            responseEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", false);
            map.put("msg", "Fail");
            responseEntity = new ResponseEntity<Map<String, Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
