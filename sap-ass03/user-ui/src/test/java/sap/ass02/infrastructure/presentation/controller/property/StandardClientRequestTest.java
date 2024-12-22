package sap.ass02.infrastructure.presentation.controller.property;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.EBike;
import sap.ass02.domain.P2d;
import sap.ass02.domain.User;
import sap.ass02.domain.V2d;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

//! END-TO-END tests
class StandardClientRequestTest {
    private static final Logger LOGGER = LogManager.getLogger(StandardClientRequestTest.class);
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
        Thread.sleep(3*MINUTE);   // cannot use process.waitFor() because it would block the thread indefinitely
        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx);
        this.clientRequest.attachWebClient(webClient, "localhost", 8080);
    }
    
    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Process process = startProcess(new File(".."), "docker-compose", "down");
        process.waitFor();
    }
    
    @Test
    void addUser() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        this.clientRequest.addUser("1", 100);
        var retrievedUser = this.clientRequest.getUser("1");
        retrievedUser.onComplete(ar -> {
            if (ar.succeeded()) {
                var user = ar.result();
                assertAll(
                        () -> assertEquals("1", user.getId()),
                        () -> assertEquals(100, user.getCredit())
                );
            } else {
                fail("User not found");
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    @Test
    void addEBike() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        this.clientRequest.addEBike("1");
        var retrievedEBike = this.clientRequest.getEBike("1");
        retrievedEBike.onComplete(ar -> {
            if (ar.succeeded()) {
                var ebike = ar.result();
                assertAll(
                        () -> assertEquals("1", ebike.getId()),
                        () -> assertEquals(EBike.EBikeState.AVAILABLE, ebike.getState()),
                        () -> assertEquals(100, ebike.getBatteryLevel()),
                        () -> assertEquals(new V2d(1.0,0.0), ebike.getDirection()),
                        () -> assertEquals(new P2d(0.0,0.0), ebike.getLocation()),
                        () -> assertEquals(0, ebike.getSpeed())
                );
            } else {
                fail("EBike not found");
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    @Test
    void addRide() {
        this.clientRequest.addUser("1", 100);
        this.clientRequest.addEBike("2");
        this.clientRequest.startRide("1", "1");
        var retrievedRide = this.clientRequest.getRide("1", "1");
        retrievedRide.onComplete(ar -> {
            if (ar.succeeded()) {
                var rideString = ar.result();
                var ride = new JsonObject(rideString);
                assertAll(
                        () -> assertEquals("12", ride.getString(JsonFieldKey.JSON_RIDE_ID_KEY)),
                        () -> assertEquals("1", ride.getString(JsonFieldKey.JSON_RIDE_USER_ID_KEY)),
                        () -> assertEquals("2", ride.getString(JsonFieldKey.EBIKE_ID_KEY))
                );
            } else {
                fail("Ride not found");
            }
        });
    }
    
    
    @Test
    void addMultipleUsers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        this.clientRequest.addUser("1", 100);
        this.clientRequest.addUser("2", 55);
        this.clientRequest.addUser("3", 11);
        this.clientRequest.getAllUsers().onComplete(ar -> {
            if (ar.succeeded()) {
                var users = (ArrayList<User>) ar.result();
                assertAll(
                        () -> assertEquals(3, users.size())
                );
            } else {
                fail("Users not found");
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    @Test
    void addMultipleEBikes() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        this.clientRequest.addEBike("1");
        this.clientRequest.addEBike("2");
        this.clientRequest.addEBike("3");
        this.clientRequest.getAllEBikes().onComplete(ar -> {
            if (ar.succeeded()) {
                var ebikes = (ArrayList<EBike>) ar.result();
                assertAll(
                        () -> assertEquals(3, ebikes.size())
                );
            } else {
                fail("EBikes not found");
            }
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
    }
    
    private static Process startProcess(File workDir, String... cmdLine) throws IOException {
        LOGGER.trace("Starting process on dir '{}', with command line: '{}'", workDir, Arrays.toString(cmdLine));
        var prefix = StandardClientRequestTest.class.getName() + "-" + Arrays.hashCode(cmdLine);
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