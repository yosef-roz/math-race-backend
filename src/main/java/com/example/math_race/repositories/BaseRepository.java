package com.example.math_race.repositories;

import com.example.math_race.entities.BaseEntity;
import com.example.math_race.entities.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Component
public class BaseRepository {

    protected SessionFactory sessionFactory;

    @Autowired
    public BaseRepository(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public void save(BaseEntity baseEntity) {
        baseEntity.setUpdatedDate(new Date());
        this.sessionFactory.getCurrentSession().saveOrUpdate(baseEntity);
    }

    public void remove(Object object) {
        this.sessionFactory.getCurrentSession().remove(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.sessionFactory.getCurrentSession().get(clazz, oid);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("FROM " + clazz.getSimpleName(), clazz)
                .list();
    }

    public <T> void saveAll(List<T> objects) {
        for (T object : objects) {
            save(object);
        }
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}