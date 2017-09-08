package com.dyrs.api.controller;

import com.dyrs.api.entity.IMMessage;
import com.dyrs.api.entity.IMUser;
import com.dyrs.api.entity.vo.LeavingMessageVO;
import com.dyrs.api.service.CustomerService;
import com.dyrs.api.service.MessageService;
import com.dyrs.api.service.RecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {
    @Value("${server.info}")
    public String server_info;
    @Value("${im.customer.secret}")
    private String secret;
    @Value("${im.customer.url}")
    private String group_url;
    @Value("${im.messages.url}")
    private String message_url;
    @Value("${im.user_info.url}")
    private String user_info_url;
    @Value("${im.user_guest.url}")
    private String guest_url;
    @Value("${im.wellcome.msg}")
    private String wellcome_msg;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private MessageService messageService;

    /**
     * 测试接口
     *
     * @return
     */
    @RequestMapping(
            value = "/info",
            method = RequestMethod.GET
    )
    public String getServer_info() {
        return server_info;
    }

    @RequestMapping(
            value = "/groups/group",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public String getCustomerServiceList() throws UnsupportedEncodingException {
        return getStringByHttp(secret, group_url, HttpMethod.GET);
    }

    @RequestMapping(
            value = "/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public String getMessages(@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("page") int page) throws UnsupportedEncodingException, JsonProcessingException {
//        String get_message_url = message_url + "?from=" + from + "&to=" + to + "&page=" + page;
//        return getStringByHttp(secret, get_message_url, HttpMethod.GET);
        int sevenDaysMessages = messageService.get7MessagesCount(from, to);
        List<IMMessage> messageList = messageService.getMessageHistory(page, from, to);
        Map<String, Object> map = new HashMap<>();
        map.put("message", messageList);
        map.put("pagesTotal", sevenDaysMessages % 30 > 0 ? sevenDaysMessages / 30 + 1 : sevenDaysMessages / 30);
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping(
            value = "/users/guest/{juid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public String getGuest(@PathVariable("juid") String jia_uid) throws UnsupportedEncodingException {
        String guest_url_full = guest_url + jia_uid;
        return getStringByHttp(secret, guest_url_full, HttpMethod.GET);
    }

    @RequestMapping(
            value = "/users/{user}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public String getUserInfo(@PathVariable("user") String userid) throws UnsupportedEncodingException {
        String user_url = user_info_url + userid;
        return getStringByHttp(secret, user_url, HttpMethod.GET);
    }

    @RequestMapping(
            value = "/customer/{jiajuid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Map<String, String>> getCustomer(@PathVariable("jiajuid") String jiajuID) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(jiajuID)) {
            map.put("error", "jiajuid must be not null or empty");
            return new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
        }
        IMUser customer = customerService.getCustomer(jiajuID);
        if (customer != null) {
            map.put("username", customer.getUsername());
            map.put("icon", customer.getIcon());
            map.put("nick", customer.getNick());
            map.put("jid", customer.getJid());
        }

        return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/record",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Map<String, String>> postLeavingMessage(@RequestBody LeavingMessageVO messageVO) {
        Long recordID = recordService.addRecord(messageVO);
        Map<String, String> map = new HashMap<>();
        map.put("id", recordID.toString());
        return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/wellcome/msg",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<Map<String, Object>> getWellcomeMessage() throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", new String(wellcome_msg.getBytes("ISO-8859-1"), "UTF-8"));
        map.put("time", System.currentTimeMillis() + "");
        return ResponseEntity.ok(map);
    }

    private String getStringByHttp(String secret, String url, HttpMethod httpMethod) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> accepts = new ArrayList<>();
        accepts.add(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(accepts);
        headers.set("Authorization", secret);
        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, new HttpEntity(headers), String.class);
        return new String(response.getBody().getBytes("ISO-8859-1"), "UTF-8");
    }


}
