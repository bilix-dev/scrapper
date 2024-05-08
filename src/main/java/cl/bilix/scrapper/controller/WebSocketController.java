package cl.bilix.scrapper.controller;

import java.util.Map;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @SubscribeMapping("/topic/notification")
    public Map<Object, Object> chatInit() {
        return ApiController.getLockerMessage();
    }

}