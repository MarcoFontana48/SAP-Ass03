package sap.ass02.domain.application;

import io.vertx.core.Verticle;
import sap.ddd.ReadOnlyRepository;
import sap.ddd.ReadWriteRepository;
import sap.ddd.Service;

public interface ServiceVerticle extends Verticle, Service {
    void attachQueryOnlyRepository(ReadOnlyRepository repository);
}
