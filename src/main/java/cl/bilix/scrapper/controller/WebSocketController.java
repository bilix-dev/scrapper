package cl.bilix.scrapper.controller;

import java.util.Map;

import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import cl.bilix.scrapper.helpers.Locker;

@Controller
public class WebSocketController {

    @SubscribeMapping("/topic/notification")
    public Map<Object, Object> chatInit() {
        return Locker.getInstance().getLockerMessage();
    }

}