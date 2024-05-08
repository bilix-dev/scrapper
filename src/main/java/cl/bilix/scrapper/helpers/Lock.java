package cl.bilix.scrapper.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Lock extends ReentrantLock implements AutoCloseable {
    private final List<Object> ids = Collections.synchronizedList(new ArrayList<Object>());
    private Object lockedId = null;

    public List<Object> getLockedIds() {
        return Collections.unmodifiableList(ids);
    }

    public HashMap<Object, Object> isLockedWithId() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("isLocked", this.isLocked());
        map.put("id", lockedId);
        return map;
    }

    public Lock open(Object id) {
        ids.add(id);
        this.lock();
        lockedId = id;
        return this;
    }

    @Override
    public void close() {
        ids.remove(lockedId);
        lockedId = null;
        this.unlock();
    }
}
