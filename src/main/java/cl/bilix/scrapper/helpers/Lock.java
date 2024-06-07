package cl.bilix.scrapper.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Lock extends ReentrantLock implements AutoCloseable {
    private final List<Object> ids = Collections.synchronizedList(new ArrayList<Object>());
    private Object lockedId = null;

    private SimpMessagingTemplate template;

    public Lock(SimpMessagingTemplate template) {
        super();
        this.template = template;
    }

    private void sendMessage() {
        template.convertAndSend("/topic/notification", Locker.getInstance().getLockerMessage());
    }

    public List<Object> getLockedIds() {
        return Collections.unmodifiableList(ids);
    }

    public HashMap<Object, Object> isLockedWithId() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("isLocked", this.isLocked());
        map.put("currentId", lockedId);
        map.put("waitingIds", ids);
        return map;
    }

    public Lock open(Object id) {
        ids.add(id);
        sendMessage();
        this.lock();
        lockedId = id;
        sendMessage();
        return this;
    }

    @Override
    public void close() {
        ids.remove(lockedId);
        lockedId = null;
        this.unlock();
        sendMessage();
    }
}
