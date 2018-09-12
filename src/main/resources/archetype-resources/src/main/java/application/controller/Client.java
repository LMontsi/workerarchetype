package ${groupId}.application.controller;


import ${groupId}.application.configuration.Constants;
import org.apache.log4j.Logger;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * this bean is where the actual camunda client and api call are implemented*/

@Component
public class Client {

    final static Logger logger = Logger.getLogger(Client.class);


    @Autowired
    Constants constants;

    public void start()
    {
        logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        logger.info("=====================WORKER STARTED====================");

        RestTemplate restTemplate = new RestTemplate();




        try {

            ExternalTaskClient client = ExternalTaskClient.create()
                    .baseUrl(constants.camundaUrl)
                    .build();

// subscribe to the topic
            client.subscribe(constants.camundaTopic)
                    .lockDuration(1000)
                    .handler((externalTask, externalTaskService) -> {


                        Integer prResponseCode = 1;

                        String ghID = externalTask.getVariable("hgid");
                        String accountNumber = externalTask.getVariable("accountNumber");
                        String holdType = externalTask.getVariable("holdType");



                        try
                        {
                            logger.info("THE PRC CODE IS : " + ghID);
                            logger.info("THE ACCOUNT NUMBER IS : " + accountNumber);
                            logger.info("THE HOLD TYPE IS : " + holdType);




                            //make the API call and validates response
                            String Reponse = restCall(restTemplate,holdType,accountNumber);

                            if(constants.httpResponseCodes.contains(Reponse))
                            {
                                prResponseCode = 0;
                            }



                            ObjectValue prCode = Variables
                                    .objectValue(prResponseCode)
                                    .create();


                            logger.info("Done creating variables");


                            // set the recently created variable
                            externalTask.setVariableTyped("errorCode", prCode);
                            logger.info("Reponse code variable set to external task");




                            logger.info("done putting them on the external task ");

                            // complete the external task
                            externalTaskService.complete(externalTask);

                            logger.info("The External Task " + externalTask.getId()
                                    + " has been completed!");
                            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            logger.info("=====================WORKER ENDED====================");
                        }
                        catch(Exception e)
                        {
                            System.out.println("inside inner camunda worker");
                            e.printStackTrace();



                            externalTaskService.handleFailure(
                                    externalTask,
                                    externalTask.getId(),
                                    "Could not lift hold on account : DT web-service not reachable",     // errorMessage
                                    0,                                                                    // retries
                                    10L * 60L * 1000L);

                            System.exit(1);

                        }

                    }).open();
        }
        catch (Exception e)
        {
            logger.info("inside camunda worker");
            e.printStackTrace();

            System.exit(1);
        }

    }

    public String restCall(RestTemplate restTemplate, String holdtype,String accountNumber) throws Exception {
        String aResponse = null;


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization",constants.authToken);
        try {

            HttpEntity<Object> entity = new HttpEntity<Object>(headers);
            ResponseEntity<String> response = restTemplate.exchange(constants.holdAPI.replace("<<tokenToReplace>>",accountNumber).replace("<<holdtype>>",holdtype), HttpMethod.DELETE, entity, String.class);
            aResponse = Integer.toString(response.getStatusCodeValue());
            logger.info(aResponse);

            if(aResponse==null || !constants.httpResponseCodes.contains(aResponse))
            {
                aResponse = response.getBody();
            }

        }
        catch(Exception e)
        {
            logger.info("in rest call exception");
            e.printStackTrace();
        }
        return  aResponse;
    }

}
