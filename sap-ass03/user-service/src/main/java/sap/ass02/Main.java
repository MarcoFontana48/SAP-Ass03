package sap.ass02;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.Controller;
import sap.ass02.domain.application.ServiceVerticle;
import sap.ass02.domain.EventManager;
import sap.ass02.infrastructure.KafkaUserServiceEventManagerVerticle;
import sap.ass02.infrastructure.RESTUserServiceControllerVerticle;
import sap.ass02.domain.application.UserServiceVerticle;
import sap.ass02.infrastructure.persistence.AbstractVerticleReadOnlyRepository;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.ReadOnlyRepositoryAdapter;
import sap.ass02.infrastructure.persistence.local.LocalJsonQueryRepositoryAdapter;
import sap.ass02.infrastructure.persistence.local.LocalJsonRepositoryAdapter;
import sap.ass02.infrastructure.persistence.mongo.MongoRepositoryAdapter;
import sap.ass02.infrastructure.persistence.sql.SQLRepositoryAdapter;

import java.util.Arrays;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        AbstractVerticleRepository readWriteRepository = new LocalJsonRepositoryAdapter();
//        AbstractVerticleRepository readWriteRepository = new MongoRepositoryAdapter();
//        AbstractVerticleRepository readWriteRepository = new SQLRepositoryAdapter();
        readWriteRepository.init();
        
        AbstractVerticleReadOnlyRepository readOnlyRepository = new ReadOnlyRepositoryAdapter(readWriteRepository);
        
        ServiceVerticle service = new UserServiceVerticle();
        service.attachRepository(readWriteRepository);
        
        Controller controller = new RESTUserServiceControllerVerticle();
        EventManager eventManager = new KafkaUserServiceEventManagerVerticle();
        
        deployVerticles(controller, eventManager, readOnlyRepository, service);
        
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
