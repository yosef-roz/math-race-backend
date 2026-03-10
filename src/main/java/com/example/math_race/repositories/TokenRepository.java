package com.example.math_race.repositories;

import com.example.math_race.entities.TokenEntity;
import com.example.math_race.entities.UserEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class TokenRepository extends BaseRepository {

    @Autowired
    public TokenRepository(SessionFactory sf) {
        super(sf);
    }

    public TokenEntity findByToken(String token) {
        String hql = "FROM TokenEntity where token = :token";

        return getCurrentSession()
                .createQuery(hql, TokenEntity.class)
                .setParameter("token", token)
                .uniqueResult();
    };

    public List<TokenEntity> findTokensByUserAndType(UserEntity user, TokenEntity.TokenType tokenType) {
        String hql = "FROM TokenEntity where  user.id = :userId and type = :tokenType";

        return getCurrentSession()
                .createQuery(hql, TokenEntity.class)
                .setParameter("userId", user.getId())
                .setParameter("tokenType",tokenType)
                .getResultList();
    }
}
