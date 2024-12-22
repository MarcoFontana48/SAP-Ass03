package sap.ass02.application;

import io.vertx.core.Verticle;
import sap.ddd.Repository;
import sap.ddd.Service;

public interface ServiceVerticle extends Verticle, Service {
    void attachQueryOnlyRepository(Repository repository);
}
