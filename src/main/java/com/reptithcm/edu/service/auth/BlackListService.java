package com.reptithcm.edu.service.auth;

import com.reptithcm.edu.entity.BlackList;
import com.reptithcm.edu.repository.BlackListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BlackListService {
    private final BlackListRepository blackListRepository;

    public void addTokenToBlackList(String token, String username) {
        BlackList blackList = new BlackList();
        blackList.setToken(token);
        blackListRepository.save(blackList);
    }

    public boolean isTokenBlacklisted(String token) {
        return blackListRepository.existsByToken(token);
    }

    public boolean isUserInBlackList(String username){
        return blackListRepository.existsByToken(username);
    }
}
