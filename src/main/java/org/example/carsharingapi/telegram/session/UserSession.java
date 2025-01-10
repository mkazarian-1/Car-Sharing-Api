package org.example.carsharingapi.telegram.session;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSession {
    private String username;
    private String password;
    private List<String> commandList;
    private LoginStep currentStep = LoginStep.WAITING_FOR_USERNAME;

    public enum LoginStep {
        WAITING_FOR_USERNAME,
        WAITING_FOR_PASSWORD,
        COMPLETE
    }
}
