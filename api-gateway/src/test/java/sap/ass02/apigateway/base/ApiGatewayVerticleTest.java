package sap.ass02.apigateway.base;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

//! INTEGRATION tests
public class ApiGatewayVerticleTest {
    
    private Vertx vertx;
    private WebClient client;
    
    @BeforeEach
    public void setUp() {
        this.vertx = Vertx.vertx();
        this.client = WebClient.create(this.vertx, new WebClientOptions().setDefaultPort(8080));
        this.vertx.deployVerticle(ApiGatewayVerticle.class.getName());
    }
    
    @AfterEach
    public void tearDown() {
        this.vertx.close();
    }
    
    @Test
    public void testEbikeServiceRedirection() {
        final String path = "/app/ebike/test";
        this.client.get(path).send(ar -> {
            if (ar.succeeded()) {
                assertEquals(200, ar.result().statusCode());
            } else {
                fail("Failed to send request to ebike service");
            }
        });
    }
    
    @Test
    public void testUserServiceRedirection() {
        final String path = "/app/user/test";
        this.client.get(path).send(ar -> {
            if (ar.succeeded()) {
                assertEquals(200, ar.result().statusCode());
            } else {
                fail("Failed to send request to user service");
            }
        });
    }
}
