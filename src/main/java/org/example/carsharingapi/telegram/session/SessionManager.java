package org.example.carsharingapi.telegram.session;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public void startSession(Long chatId, List<String> commandList) {
        UserSession newUserSession = new UserSession();
        newUserSession.setCommandList(commandList);
        userSessions.put(chatId, newUserSession);
    }

    public UserSession getSession(Long chatId) {
        return userSessions.get(chatId);
    }

    public boolean isSession(Long chatId){
        return userSessions.containsKey(chatId);
    }

    public void endSession(Long chatId) {
        userSessions.remove(chatId);
    }
}