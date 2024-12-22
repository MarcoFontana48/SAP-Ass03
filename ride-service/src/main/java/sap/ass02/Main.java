package sap.ass02;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.application.RideServiceVerticle;
import sap.ass02.application.ServiceVerticle;
import sap.ass02.domain.Controller;
import sap.ass02.domain.EventManager;
import sap.ass02.infrastructure.KafkaRideServiceEventManagerVerticle;
import sap.ass02.infrastructure.RESTRideServiceControllerVerticle;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.local.LocalJsonQueryRepositoryAdapter;
import sap.ass02.infrastructure.persistence.local.LocalJsonRepositoryAdapter;

import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        AbstractVerticleRepository repository = new LocalJsonRepositoryAdapter();
//        AbstractVerticleRepository repository = new MongoRepositoryAdapter();
//        AbstractVerticleRepository repository = new SQLRepositoryAdapter();
        repository.init();
        
        AbstractVerticleRepository queryOnlyRepository = new LocalJsonQueryRepositoryAdapter();
//        AbstractVerticleRepository queryOnlyRepository = new MongoRepositoryAdapter();
//        AbstractVerticleRepository queryOnlyRepository = new SQLQueryRepositoryAdapter();
        queryOnlyRepository.init();
        
        ServiceVerticle service = new RideServiceVerticle();
        service.attachRepository(repository);
        service.attachQueryOnlyRepository(queryOnlyRepository);
        
        Controller controller = new RESTRideServiceControllerVerticle();
        EventManager eventManager = new KafkaRideServiceEventManagerVerticle();
        
        deployVerticles(controller, eventManager, queryOnlyRepository, service);
        
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