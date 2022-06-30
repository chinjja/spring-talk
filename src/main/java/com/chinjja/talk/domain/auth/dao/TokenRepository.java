package com.chinjja.talk.domain.auth.dao;

import org.springframework.data.repository.Repository;

import com.chinjja.talk.domain.auth.model.Token;
import com.chinjja.talk.domain.user.model.User;

public interface TokenRepository extends Repository<Token, String> {
    Token save(Token token);
    void delete(Token token);
    void deleteByUser(User user);
    Token findByUser(User user);
}
