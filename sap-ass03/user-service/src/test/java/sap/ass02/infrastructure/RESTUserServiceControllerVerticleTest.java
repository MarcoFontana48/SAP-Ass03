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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

//! INTEGRATION tests
class RESTUserServiceControllerVerticleTest {
    private static final Logger LOGGER = LogManager.getLogger(RESTUserServiceControllerVerticleTest.class);
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
        Thread.sleep(MINUTE);   // cannot use process.waitFor() because it would block the thread indefinitely
        this.client = HttpClient.newHttpClient();
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
         Process process = startProcess(new File("."), "docker-compose", "down");
         process.waitFor();
    }
    
    @Test
    void insertsUserCorrectly() throws IOException, InterruptedException {
        // Send POST request
        JsonObject json = new JsonObject()
                .put("user_id", "1")
                .put("credit", 100);
        
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> postResponse = this.client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(STATUS_CODE_OK, postResponse.statusCode());
    }
    
    @Test
    void retrievesSingleUserCorrectly() throws IOException, InterruptedException {
        // Send POST request
        JsonObject json = new JsonObject()
                .put("user_id", "1")
                .put("credit", 100);
        
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> postResponse = this.client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user/"))
                .GET()
                .build();
        HttpResponse<String> getResponse = this.client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        // Validate response
        JsonArray expectedResponse = new JsonArray();
        expectedResponse.add(JsonObject.of("user_id", "1", "credit", 100));
        
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode()),
                () -> assertEquals(STATUS_CODE_OK, postResponse.statusCode()),
                () -> assertEquals(expectedResponse.toString(), getResponse.body())
        );
    }
    
    @Test
    void retrievesMultipleUsersCorrectly() throws IOException, InterruptedException {
        // Send POST requests
        JsonObject json1 = new JsonObject()
                .put("user_id", "1")
                .put("credit", 100);
        JsonObject json2 = new JsonObject()
                .put("user_id", "2")
                .put("credit", 100);
        
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json1.toString()))
                .build();
        HttpResponse<String> postResponse1 = this.client.send(postRequest1, HttpResponse.BodyHandlers.ofString());
        
        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json2.toString()))
                .build();
        HttpResponse<String> postResponse2 = this.client.send(postRequest2, HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user/"))
                .GET()
                .build();
        HttpResponse<String> getResponse = this.client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        // Validate response
        JsonArray expectedResponse = new JsonArray();
        JsonObject expectedResponseJsonObject1 = JsonObject.of("user_id", "1", "credit", 100);
        JsonObject expectedResponseJsonObject2 = JsonObject.of("user_id", "2", "credit", 100);
        expectedResponse.add(expectedResponseJsonObject1);
        expectedResponse.add(expectedResponseJsonObject2);
        JsonArray actualResponse = new JsonArray(getResponse.body());
        if (actualResponse.getJsonObject(0).getString("user_id").equals("2")) {
            JsonObject temp = actualResponse.getJsonObject(0);
            actualResponse.set(0, actualResponse.getJsonObject(1));
            actualResponse.set(1, temp);
        } else {
            actualResponse.set(0, actualResponse.getJsonObject(0));
            actualResponse.set(1, actualResponse.getJsonObject(1));
        }
        
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode()),
                () -> assertEquals(STATUS_CODE_OK, postResponse1.statusCode()),
                () -> assertEquals(STATUS_CODE_OK, postResponse2.statusCode()),
                () -> assertEquals(expectedResponse, actualResponse)
        );
    }
    
    @Test
    void updatesUserCreditCorrectly() throws IOException, InterruptedException {
        // Send POST request
        JsonObject json = new JsonObject()
                .put("user_id", "1")
                .put("credit", 100);
        
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> postResponse = this.client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        
        // Send PUT request
        JsonObject json2 = new JsonObject()
                .put("user_id", "1")
                .put("credit", 55);
        
        HttpRequest putRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json2.toString()))
                .build();
        HttpResponse<String> putResponse = this.client.send(putRequest, HttpResponse.BodyHandlers.ofString());
        
        // Send GET request
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + this.host + ":" + this.port + "/app/user/"))
                .GET()
                .build();
        HttpResponse<String> getResponse = this.client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        // Validate response
        JsonArray expectedResponse = new JsonArray();
        expectedResponse.add(JsonObject.of("user_id", "1", "credit", 55));
        
        assertAll(
                () -> assertEquals(STATUS_CODE_OK, getResponse.statusCode()),
                () -> assertEquals(STATUS_CODE_OK, postResponse.statusCode()),
                () -> assertEquals(STATUS_CODE_OK, putResponse.statusCode()),
                () -> assertEquals(expectedResponse.toString(), getResponse.body())
        );
    }
    
    private static Process startProcess(File workDir, String... cmdLine) throws IOException {
        LOGGER.trace("Starting process on dir '{}', with command line: '{}'", workDir, Arrays.toString(cmdLine));
        var prefix = RESTUserServiceControllerVerticleTest.class.getName() + "-" + Arrays.hashCode(cmdLine);
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
