package com.ewp.crm.repository.impl;

import com.ewp.crm.models.PersistentLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

@Repository
@Transactional
public class PersistentLoginRepositoryImpl implements PersistentTokenRepository {

    private static Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public PersistentLoginRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken persistentRememberMeToken) {
        PersistentLogin login = new PersistentLogin();
        login.setUsername(persistentRememberMeToken.getUsername());
        login.setSeries(persistentRememberMeToken.getSeries());
        login.setToken(persistentRememberMeToken.getTokenValue());
        login.setLastUsed(persistentRememberMeToken.getDate());
        entityManager.persist(login);
        entityManager.flush();
    }

    @Override
    public void updateToken(String s, String tokenValue, Date date) {
        PersistentLogin login = entityManager.createQuery("SELECT pl FROM PersistentLogin pl WHERE pl.series = :seriesId", PersistentLogin.class)
                .setParameter("seriesId", s)
                .getSingleResult();
        login.setToken(tokenValue);
        login.setLastUsed(date);
        entityManager.flush();
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String s) {
        try {
            PersistentLogin login = entityManager.createQuery("SELECT pl FROM PersistentLogin pl WHERE pl.series = :seriesId", PersistentLogin.class)
                    .setParameter("seriesId", s)
                    .getSingleResult();
            if (login != null) {
                return new PersistentRememberMeToken(login.getUsername(), login.getSeries(), login.getToken(), login.getLastUsed());
            }
        } catch (NoResultException e) {
            logger.error("No entity for persistent token: " + s, e);
        }
        return null;
    }

    @Override
    public void removeUserTokens(String s) {
        entityManager.createQuery("DELETE FROM PersistentLogin pl WHERE pl.series = :seriesId")
                .setParameter("seriesId", s)
                .executeUpdate();
    }
}
