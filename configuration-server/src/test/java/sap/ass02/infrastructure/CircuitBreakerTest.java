package sap.ass02.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

//! UNIT tests
class CircuitBreakerTest {
    
    private static final int RESET_TIMEOUT = 2000;
    private static final int TIMEOUT = 1000;
    private static final int MAX_FAILURES = 3;
    private CircuitBreaker circuitBreaker;
    
    @BeforeEach
    void setUp() {
        this.circuitBreaker = new CircuitBreaker.Builder()
                .setMaxFailures(MAX_FAILURES)
                .setTimeout(TIMEOUT)
                .setResetTimeout(RESET_TIMEOUT)
                .build();
    }
    
    @Test
    void testCircuitBreakerClosedInitially() {
        assertFalse(this.circuitBreaker.isOpen());
    }
    
    @Test
    void testCircuitBreakerOpensAfterMaxFailures() {
        for (int i = 0; i < MAX_FAILURES; i++) {
            try {
                this.circuitBreaker.execute(() -> {
                    throw new RuntimeException("Failure");
                }).join();
            } catch (Exception ignored) {
            }
        }
        assertTrue(this.circuitBreaker.isOpen());
    }
    
    @Test
    void testCircuitBreakerResetsAfterTimeout() throws InterruptedException {
        for (int i = 0; i < MAX_FAILURES; i++) {
            try {
                this.circuitBreaker.execute(() -> {
                    throw new RuntimeException("Failure");
                }).join();
            } catch (Exception ignored) {
            }
        }
        var isOpenBeforeReset = this.circuitBreaker.isOpen();
        
        // Wait for reset timeout
        int delay = 1000;
        Thread.sleep(RESET_TIMEOUT + delay);
        
        assertAll(
                () -> assertTrue(isOpenBeforeReset),
                () -> assertFalse(this.circuitBreaker.isOpen())
        );
    }
    
    @Test
    void testSuccessfulExecutionResetsFailureCount() {
        try {
            this.circuitBreaker.execute(() -> {
                throw new RuntimeException("Failure");
            }).join();
        } catch (Exception ignored) {
        }
        
        int failureCountBeforeReset = this.circuitBreaker.getFailureCount();
        this.circuitBreaker.execute(() -> "Success").join();
        assertEquals(0, this.circuitBreaker.getFailureCount());
        
        assertAll(
                () -> assertEquals(1, failureCountBeforeReset),
                () -> assertEquals(0, this.circuitBreaker.getFailureCount())
        );
    }
    
    @Test
    void testExecuteReturnsResult() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> future = this.circuitBreaker.execute(() -> "Success");
        assertEquals("Success", future.get(1, TimeUnit.SECONDS));
    }
    
    @Test
    void testExecuteThrowsExceptionWhenOpen() {
        for (int i = 0; i < MAX_FAILURES; i++) {
            try {
                this.circuitBreaker.execute(() -> {
                    throw new RuntimeException("Failure");
                }).join();
            } catch (Exception ignored) {
            }
        }
        var isOpenBeforeExecute = this.circuitBreaker.isOpen();
        CompletableFuture<String> future = this.circuitBreaker.execute(() -> "Success");
        
        assertAll(
                () -> assertTrue(isOpenBeforeExecute),
                () -> assertThrows(ExecutionException.class, () -> future.get(1, TimeUnit.SECONDS))
        );
    }
}
