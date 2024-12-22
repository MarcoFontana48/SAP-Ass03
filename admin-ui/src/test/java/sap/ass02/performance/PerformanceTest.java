package sap.ass02.performance;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.infrastructure.presentation.controller.property.ClientRequest;
import sap.ass02.infrastructure.presentation.controller.property.StandardClientRequest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

//! END-TO-END tests
class PerformanceTest {
    private static final Logger LOGGER = LogManager.getLogger(PerformanceTest.class);
    private static final int MINUTE = 60_000;
    private final ClientRequest clientRequest = new StandardClientRequest();
    
    @BeforeAll
    static void setUpAll() throws InterruptedException, IOException {
        LOGGER.trace("Tearing down containers before testing, if they are running...");
        Process process = startProcess(new File(".."), "docker-compose", "down");
        process.waitFor();
    }
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        startProcess(new File(".."), "docker-compose", "up");
        Thread.sleep(2*MINUTE);   // cannot use process.waitFor() because it would block the thread indefinitely
        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx);
        this.clientRequest.attachWebClient(webClient, "localhost", 8080);
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker-compose", "down");
        process.waitFor();
    }
    
    //simulates a single admin sending multiple requests to the server
    @Test
    void evaluatePerformanceOnNormalEnvironment() throws InterruptedException, MalformedURLException {
        CountDownLatch userLatch = new CountDownLatch(1);
        CountDownLatch ebikeLatch = new CountDownLatch(1);
        
        // send multiple requests
        this.clientRequest.addUser("1", 100);
        this.clientRequest.addEBike("1");
        
        this.clientRequest.getAllUsers().onComplete(ar -> userLatch.countDown());
        this.clientRequest.getAllEBikes().onComplete(ar -> ebikeLatch.countDown());
        
        userLatch.await();
        ebikeLatch.await();
        
        // get the avg latency from Prometheus
        PrometheusMetricsParser parser = new PrometheusMetricsParser();
        double avgLatencyUserService = parser.getAverageLatencyUserService();
        LOGGER.trace("Average Latency user-service: {}", avgLatencyUserService);
        double avgLatencyEBikeService = parser.getAverageLatencyEBikeService();
        LOGGER.trace("Average Latency ebike-service: {}", avgLatencyEBikeService);
        
        assertAll(
                () -> assertTrue(avgLatencyUserService < 0.1),
                () -> assertTrue(avgLatencyEBikeService < 0.1)
        );
    }
    
    //simulates multiple admins sending multiple requests to the server
    @Test
    void evaluatePerformanceOnOverloadedEnvironment() throws InterruptedException, MalformedURLException {
        int numberOfAdminsSendingAllRequests = 100;
        CountDownLatch userLatch = new CountDownLatch(numberOfAdminsSendingAllRequests);
        CountDownLatch ebikeLatch = new CountDownLatch(numberOfAdminsSendingAllRequests);
        
        // send multiple requests
        for (int i = 0; i < numberOfAdminsSendingAllRequests; i++) {
            this.clientRequest.addUser(String.valueOf(i), 100);
            this.clientRequest.addEBike(String.valueOf(i));
            
            this.clientRequest.getAllUsers().onComplete(ar -> userLatch.countDown());
            this.clientRequest.getAllUsers().onComplete(ar -> ebikeLatch.countDown());
        }
        
        userLatch.await();
        ebikeLatch.await();
        
        // get the avg latency from Prometheus
        PrometheusMetricsParser parser = new PrometheusMetricsParser();
        double avgLatencyUserService = parser.getAverageLatencyUserService();
        LOGGER.trace("Average Latency user-service: {}", avgLatencyUserService);
        double avgLatencyEBikeService = parser.getAverageLatencyEBikeService();
        LOGGER.trace("Average Latency ebike-service: {}", avgLatencyEBikeService);
        
        assertAll(
                () -> assertTrue(avgLatencyUserService < 0.3),
                () -> assertTrue(avgLatencyEBikeService < 0.3)
        );
    }
    
    private static Process startProcess(File workDir, String... cmdLine) throws IOException {
        LOGGER.trace("Starting process on dir '{}', with command line: '{}'", workDir, Arrays.toString(cmdLine));
        var prefix = PerformanceTest.class.getName() + "-" + Arrays.hashCode(cmdLine);
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