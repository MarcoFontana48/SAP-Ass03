package sap.ass02;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.application.RideServiceVerticle;
import sap.ass02.domain.application.ServiceVerticle;
import sap.ass02.domain.Controller;
import sap.ass02.domain.EventManager;
import sap.ass02.infrastructure.KafkaRideServiceEventManagerVerticle;
import sap.ass02.infrastructure.RESTRideServiceControllerVerticle;
import sap.ass02.infrastructure.persistence.local.LocalJsonRepositoryAdapter;
import sap.ddd.Repository;

import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        Repository repository = new LocalJsonRepositoryAdapter();
//        Repository repository = new MongoRepositoryAdapter();
//        Repository repository = new SQLRepositoryAdapter();
        repository.init();
        
        ServiceVerticle service = new RideServiceVerticle();
        service.attachRepository(repository);
        
        Controller controller = new RESTRideServiceControllerVerticle();
        EventManager eventManager = new KafkaRideServiceEventManagerVerticle();
        
        deployVerticles(controller, eventManager, service);
        
        controller.attachService(service);
    }
    
    private static void deployVerticles(Verticle... verticle) {
        Vertx vertx = Vertx.vertx();
        
        Arrays.stream(verticle).forEach( v -> vertx.deployVerticle(v, res -> {
            if (res.succeeded()) {
                LOGGER.info("{} started", v.getClass().getSimpleName());
            } else {
                LOGGER.error("{} failed to start", v.getClass().getSimpleName(), res.cause());
            }
        }));
    }
}