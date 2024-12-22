package sap.ass02.domain.port;

import sap.ass02.domain.entity.MongoCredentials;
import sap.ass02.domain.entity.SQLCredentials;

public interface Service extends Port {
    SQLCredentials getUserServiceSqlCredentials();
    
    MongoCredentials getUserServiceMongoCredentials();
    
    SQLCredentials getEBikeServiceSqlCredentials();
    
    MongoCredentials getEBikeServiceMongoCredentials();
}
