package com.example.math_race.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BaseRepository {

    @Autowired
    protected SessionFactory sessionFactory;

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
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
            this.sessionFactory.getCurrentSession().saveOrUpdate(object);
        }
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}