package sap.ass02.domain;

import io.vertx.core.Verticle;
import sap.ddd.Service;

public interface Controller extends Verticle {
    
    void attachService(Service service);
}
