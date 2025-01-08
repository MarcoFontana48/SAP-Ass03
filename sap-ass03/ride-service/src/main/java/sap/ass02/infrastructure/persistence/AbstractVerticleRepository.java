package sap.ass02.infrastructure.persistence;

import io.vertx.core.AbstractVerticle;
import sap.ass02.domain.dto.EBikeDTO;
import sap.ass02.domain.dto.UserDTO;
import sap.ddd.ReadWriteRepository;

public abstract class AbstractVerticleRepository extends AbstractVerticle implements ReadWriteRepository {

}
