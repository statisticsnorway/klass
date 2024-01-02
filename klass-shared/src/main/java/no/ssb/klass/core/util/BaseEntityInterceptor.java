package no.ssb.klass.core.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import no.ssb.klass.core.model.BaseEntity;

/**
 * A hibernate interceptor used to instantiate entities. This to avoid JPAs requirement that entities must have a no
 * argument constructor. Hence by using this interceptor the entities do not require a no argument constructor.
 * 
 * @author karsten.vileid
 *
 */
public class BaseEntityInterceptor extends EmptyInterceptor {
    private final Objenesis objenesis;
    private Map<String, Optional<Class<BaseEntity>>> clazzCache;

    public BaseEntityInterceptor() {
        objenesis = new ObjenesisStd(true);
        clazzCache = new ConcurrentHashMap<String, Optional<Class<BaseEntity>>>();
    }

    @Override
    public BaseEntity instantiate(String entityName, EntityMode entityMode, Serializable id) {
        Optional<Class<BaseEntity>> clazz = getOrUpdate(entityName);
        if (!clazz.isPresent()) {
            return null;
        }
        BaseEntity entity = objenesis.getInstantiatorOf(clazz.get()).newInstance();
        entity.setId((Long) id);
        entity.init();
        return entity;
    }

    private Optional<Class<BaseEntity>> getOrUpdate(String entityName) {
        return clazzCache.computeIfAbsent(entityName, k -> getBaseEntityClass(entityName));
    }

    @SuppressWarnings("unchecked")
    private Optional<Class<BaseEntity>> getBaseEntityClass(String clazz) {
        try {
            Class<?> foundClazz = Class.forName(clazz);
            if (!BaseEntity.class.isAssignableFrom(foundClazz)) {
                return Optional.ofNullable(null);
            }
            return Optional.of((Class<BaseEntity>) foundClazz);
        } catch (ClassNotFoundException e) {
            return Optional.ofNullable(null);
        }
    }
}