package uk.ac.ebi.ena.webin.uploader;

import uk.ac.ebi.ena.authentication.client.AuthenticationClient;
import uk.ac.ebi.ena.authentication.client.AuthenticationClientImpl;
import uk.ac.ebi.ena.authentication.exception.AuthException;
import uk.ac.ebi.ena.authentication.model.AuthRealm;
import uk.ac.ebi.ena.authentication.model.AuthResult;

import java.util.ArrayList;
import java.util.Arrays;

public class AuthClient {

    private static final String AUTH_SERVICE_URL =  "https://www.ebi.ac.uk/ena/auth";

    String getWebinAccount(String userName, String password) {
        if(userName == null)
            return null;
        if(userName.startsWith("Webin")){
            return userName;
        }
        AuthResult authResult;
        try {
            AuthenticationClient authClient = new AuthenticationClientImpl(AUTH_SERVICE_URL);
            authResult = authClient.sessionlessLogin(userName, password,
                    new ArrayList<AuthRealm>(Arrays.asList(AuthRealm.EGA, AuthRealm.SRA)));
        } catch (AuthException e) {
           return null;
        }
        return (authResult != null && authResult.getAuthenticated() )? authResult.getLoginName().replace("WEBIN","Webin") : null;
    }
}
