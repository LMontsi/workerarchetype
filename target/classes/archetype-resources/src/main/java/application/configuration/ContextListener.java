package ${groupId}.application.configuration;

import ${groupId}.application.controller.Client;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This bean has the application context listener
 * Once the application context is fully loaded
 * the listener will start and hence initiate subscription*/

@Component
public class ContextListener {
    final static Logger logger = Logger.getLogger(ContextListener.class);

    @Autowired
    Client client;


    //Context listener to initialize subscription to cammunda
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            client.start();
        }
        catch(Exception e)
        {
            logger.info("in outer start exception");
            e.printStackTrace();
            throw e;
        }
    }
}
