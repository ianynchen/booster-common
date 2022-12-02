package io.github.booster.commons.pool;

import arrow.core.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Object pool that allows objects to be retrieved by names.
 */
public abstract class NamedObjectPool<T> {

    private static final Logger LOG = LoggerFactory.getLogger(NamedObjectPool.class);

    private final Map<String, T> cachedObjects = new HashMap<>();

    abstract protected T createObject(String name);

    public Option<T> getOption(String name) {
        LOG.debug("booster-commons - get optional for: [{}]", name);
        return Option.fromNullable(this.get(name));
    }

    public T get(String name) {
        synchronized(this.cachedObjects) {
            LOG.debug("booster-commons - get object for: [{}]", name);
            if (this.cachedObjects.containsKey(name)) {
                LOG.debug("booster-commons - named object: [{}] exists in cache", name);
                return this.cachedObjects.get(name);
            } else {
                LOG.debug("booster-commons - creating named object: [{}]", name);
                T obj = this.createObject(name);
                if (obj != null) {
                    LOG.debug("booster-commons - named object: [{}] created", name);
                    this.cachedObjects.put(name, obj);
                }
                LOG.debug("booster-commons - named object: [{}] not created", name);
                return obj;
            }
        }
    }
}
