package sap.ass02.infrastructure;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

//! COMPONENT tests
class RESTEBikeServiceControllerVerticleTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTEBikeServiceControllerVerticleTest.class);
    private static final int MINUTE = 60_000;
    private static final int STATUS_CODE_OK = 200;
    private final int port = 8080;
    private final String host = "localhost";
    private HttpClient client;
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File("."), "docker-compose", "down");
        process.waitFor();
    }
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File("."), "docker-compose", "up");
        Thread.sleep(5*MINUTE);
        this.client = HttpClient.newHttpClient();
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
//        Process process = startProcess(new File("."), "docker-compose", "down");
//        process.waitFor();
    }
    
    @Test
    void insertsEBikeCorrectly() throws IOException, InterruptedException {
        // Send POST request
        var jsonRequest = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 100)
                .toString();
        
        var postRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        
        var postResponse = this.client.send(postRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        assertEquals(STATUS_CODE_OK, postResponse.statusCode(), "POST request failed");
    }
    
    @Test
    void retrievesSingleEBikeCorrectly() throws IOException, InterruptedException {
        // Send POST request
        var jsonRequest = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 100)
                .toString();
        
        var postRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        var postResponse = this.client.send(postRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        var getRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike/"))
                .GET()
                .build();
        var getResponse = this.client.send(getRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        var actual = new JsonArray(getResponse.body()).getJsonObject(0);
        
        // Validate response
        var expectedResponse = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 1);
        
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode(), "GET request failed"),
                () -> assertEquals(STATUS_CODE_OK, postResponse.statusCode(), "POST request failed"),
                () -> assertEquals(expectedResponse.getString("ebike_id"), actual.getString("ebike_id")),
                () -> assertEquals(expectedResponse.getString("state"), actual.getString("state")),
                () -> assertEquals(expectedResponse.getString("x_location"), actual.getString("x_location")),
                () -> assertEquals(expectedResponse.getString("y_location"), actual.getString("y_location")),
                () -> assertEquals(expectedResponse.getString("x_direction"), actual.getString("x_direction")),
                () -> assertEquals(expectedResponse.getString("y_direction"), actual.getString("y_direction")),
                () -> assertEquals(expectedResponse.getString("speed"), actual.getString("speed")),
                () -> assertEquals(expectedResponse.getString("battery"), actual.getString("battery"))
        );
    }
    
    @Test
    void retrievesMultipleEBikesCorrectly() throws IOException, InterruptedException {
        // Send POST requests
        var json1 = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 1)
                .toString();
        var json2 = new JsonObject()
                .put("ebike_id", "2")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 1)
                .toString();
        
        var postRequest1 = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json1))
                .build();
        var postResponse1 = this.client.send(postRequest1, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        var postRequest2 = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json2))
                .build();
        var postResponse2 = this.client.send(postRequest2, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        var getRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike/"))
                .GET()
                .build();
        var getResponse = this.client.send(getRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        // Validate response
        var actual = new JsonArray(getResponse.body());
        
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode(), "GET request failed"),
                () -> assertEquals(STATUS_CODE_OK, postResponse1.statusCode(), "POST request failed"),
                () -> assertEquals(STATUS_CODE_OK, postResponse2.statusCode(), "POST request failed"),
                () -> assertEquals(2, actual.size(), "Response body is not as expected")
        );
    }
    
    @Test
    void updatesEBikeBatteryCorrectly() throws IOException, InterruptedException {
        // Send POST request
        var jsonRequest = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "AVAILABLE")
                .put("x_location", 0.0)
                .put("y_location", 0.0)
                .put("x_direction", 1.0)
                .put("y_direction", 0.0)
                .put("speed", 0.0)
                .put("battery", 1)
                .toString();
        
        var postRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        var postResponse = this.client.send(postRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        // Send PUT request
        var json2 = new JsonObject()
                .put("ebike_id", "1")
                .put("state", "MAINTENANCE")
                .put("x_location", 1.0)
                .put("y_location", 2.0)
                .put("x_direction", 3.0)
                .put("y_direction", 4.0)
                .put("speed", 5.0)
                .put("battery", 55);
        
        var putRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike"))
                .header("Content-Type", "application/json")
                .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(String.valueOf(json2)))
                .build();
        var putResponse = this.client.send(putRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        var getRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://" + this.host + ":" + this.port + "/app/ebike/"))
                .GET()
                .build();
        var getResponse = this.client.send(getRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
        var actual = new JsonArray(getResponse.body()).getJsonObject(0);
        
        // Validate response
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode(), "GET request failed"),
                () -> assertEquals(STATUS_CODE_OK, postResponse.statusCode(), "POST request failed"),
                () -> assertEquals(STATUS_CODE_OK, putResponse.statusCode(), "PUT request failed"),
                () -> assertEquals(json2.getString("ebike_id"), actual.getString("ebike_id")),
                () -> assertEquals(json2.getString("state"), actual.getString("state")),
                () -> assertEquals(json2.getString("x_location"), actual.getString("x_location")),
                () -> assertEquals(json2.getString("y_location"), actual.getString("y_location")),
                () -> assertEquals(json2.getString("x_direction"), actual.getString("x_direction")),
                () -> assertEquals(json2.getString("y_direction"), actual.getString("y_direction")),
                () -> assertEquals(json2.getString("speed"), actual.getString("speed")),
                () -> assertEquals(json2.getString("battery"), actual.getString("battery"))
        );
    }
    
    private static Process startProcess(File workDir, String... cmdLine) throws IOException {
        LOGGER.trace("Starting process on dir '{}', with command line: '{}'", workDir, Arrays.toString(cmdLine));
        var prefix = RESTEBikeServiceControllerVerticleTest.class.getName() + "-" + Arrays.hashCode(cmdLine);
        var stdOut = File.createTempFile(prefix + "-stdout", ".txt");
        stdOut.deleteOnExit();
        var stdErr = File.createTempFile(prefix + "-stderr", ".txt");
        stdErr.deleteOnExit();
        return new ProcessBuilder(cmdLine)
                .redirectOutput(ProcessBuilder.Redirect.to(stdOut))
                .redirectError(ProcessBuilder.Redirect.to(stdErr))
                .directory(workDir)
                .start();
    }
    
}