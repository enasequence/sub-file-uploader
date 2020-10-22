package uk.ac.ebi.ena.webin.uploader;



import java.util.ArrayList;
import java.util.Arrays;
import uk.ac.ebi.ena.webinauth.AuthenticationApiApi;
import uk.ac.ebi.ena.webinauth.client.WebinAuthConstants;
import uk.ac.ebi.ena.webinauth.client.model.AuthRequest;
import uk.ac.ebi.ena.webinauth.client.model.AuthRequest.AuthRealmsEnum;
import uk.ac.ebi.ena.webinauth.client.model.AuthResponse;

public class AuthClient {

    String getWebinAccount(String userName, String password) {
        if(userName == null)
            return null;
        if(userName.startsWith("Webin")){
            return userName;
        }
        AuthResponse authResponse;
        try {
            AuthenticationApiApi api = new AuthenticationApiApi();
            api.getApiClient().setBasePath(WebinAuthConstants.PROD_AUTH_URL);
            AuthRequest authRequest=new AuthRequest();
            authRequest.setUsername(userName);
            authRequest.setPassword(password);
            authRequest.setAuthRealms(new ArrayList<AuthRealmsEnum>(Arrays.asList(AuthRealmsEnum.EGA, AuthRealmsEnum.ENA)));
            authResponse = api.login(authRequest);
        } catch (Exception e) {
            return null;
        }
        return (authResponse != null && authResponse.getAuthenticated() )? authResponse.getLoginName().replace("WEBIN","Webin") : null;
    }
}
