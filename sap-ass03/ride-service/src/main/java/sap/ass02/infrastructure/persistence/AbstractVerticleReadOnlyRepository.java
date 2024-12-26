package sap.ass02.infrastructure.persistence;

import io.vertx.core.AbstractVerticle;
import sap.ddd.ReadOnlyRepository;

public abstract class AbstractVerticleReadOnlyRepository extends AbstractVerticle implements ReadOnlyRepository {
}
