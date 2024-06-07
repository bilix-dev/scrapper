package cl.bilix.scrapper.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Locker {

    private final HashMap<String, Lock> locker = new HashMap<>();
    private static Locker instance;

    public static Locker getInstance() {
        if (instance == null) {
            instance = new Locker();
        }
        return instance;
    }

    public Map<Object, Object> getLockerMessage() {
        return locker.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        k -> k.getKey(),
                        v -> v.getValue().isLockedWithId()));
    }

    public Lock add(String key, SimpMessagingTemplate template) {
        return locker.put(key, new Lock(template));
    }

    public Lock lock(String key, Object id) {
        // Enviar Mensaje
        return locker.get(key).open(id);
    }

    public boolean isLockedId(String key, Object id) {
        return locker.get(key).getLockedIds().contains(id);
    }

}
