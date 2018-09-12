package ${groupId}.application.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * this bean holds all that from the application properties file*/

@Component
public class Constants {

    @Value("${camunda.url}")
    public  String camundaUrl;

    @Value("${wso2.rest.api}")
    public String wso2RestApi;

    @Value("${camunda.topic}")
    public  String camundaTopic;

    @Value("${httpheader.auth.token}")
    public String authToken;

    //use SPEL to read list from properties file
    @Value("#{'${http.response.codes}'.split(',')}")
    public List<String> httpResponseCodes;

    @Value("${hold.account.api}")
    public String holdAPI;
}
