package sap.ass02;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.Controller;
import sap.ass02.domain.EventManager;
import sap.ass02.infrastructure.KafkaEBikeServiceEventManagerVerticle;
import sap.ass02.infrastructure.RESTEBikeServiceControllerVerticle;
import sap.ass02.domain.application.BikeServiceVerticle;
import sap.ass02.domain.application.ServiceVerticle;
import sap.ass02.infrastructure.persistence.AbstractVerticleRepository;
import sap.ass02.infrastructure.persistence.local.LocalJsonQueryRepositoryAdapter;
import sap.ass02.infrastructure.persistence.sql.SQLRepositoryAdapter;

import java.util.Arrays;

public final class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) {
//        AbstractVerticleRepository repository = new LocalJsonRepositoryAdaptor();
        AbstractVerticleRepository repository = new SQLRepositoryAdapter();
        repository.init();
        
        AbstractVerticleRepository queryOnlyRepository = new LocalJsonQueryRepositoryAdapter();
//        AbstractVerticleRepository queryOnlyRepository = new SQLQueryRepositoryAdapter();
        queryOnlyRepository.init();
        
        ServiceVerticle service = new BikeServiceVerticle();
        service.attachRepository(repository);
        service.attachQueryOnlyRepository(queryOnlyRepository);
        
        Controller controller = new RESTEBikeServiceControllerVerticle();
        controller.attachService(service);
        EventManager eventManager = new KafkaEBikeServiceEventManagerVerticle();
        
        deployVerticles(controller, queryOnlyRepository, service, eventManager);
    }
    
    private static void deployVerticles(Verticle... verticle) {
        Vertx vertx = Vertx.vertx();
        
        Arrays.stream(verticle).forEach(v -> vertx.deployVerticle(v, res -> {
            if (res.succeeded()) {
                LOGGER.info("{} started", v.getClass().getSimpleName());
            } else {
                LOGGER.error("{} failed to start", v.getClass().getSimpleName(), res.cause());
            }
        }));
    }
}
