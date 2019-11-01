package com.kingston.jforgame.admin.gamecmd.service;

import com.kingston.jforgame.admin.domain.ServerInfo;
import com.kingston.jforgame.admin.gamenode.service.ServerNodeService;
import com.kingston.jforgame.admin.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameCmdService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerNodeService serversManager;

    public String hotSwap(List<Integer> servers) {
        if (!SecurityUtils.hasAuth("ADMIN")) {
            return "权限不够";
        }
        // TODO 改成异步多线程执行！！
        Map<Integer, String> result = new HashMap<>();
        for (Integer serverId : servers) {
            ServerInfo server = serversManager.getServerNodeBy(serverId);
            if (server != null) {
                try {
                    String url = String.format("http://localhost:%s/serverController/hotSwap", server.getHttpPort());
                    String info = hotSwap(url);
                    result.put(serverId, info);
                } catch (Exception e) {
                    result.put(serverId, e.getMessage());
                }
            }
        }
        return result.toString();
    }

    private String hotSwap(String url) {
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        return result.getBody();
    }

}
