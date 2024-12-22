package sap.ass02.infrastructure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class CircuitBreaker {
    private static final Logger LOGGER = LogManager.getLogger(CircuitBreaker.class);
    private final int maxFailures;
    private final int timeout;
    private final int resetTimeout;
    private int failureCount = 0;
    private State state = State.CLOSED;
    
    private CircuitBreaker(Builder builder) {
        this.maxFailures = builder.maxFailures;
        this.timeout = builder.timeout;
        this.resetTimeout = builder.resetTimeout;
        this.state = builder.state;
    }
    
    public synchronized void open() {
        LOGGER.info("Circuit breaker opened");
        this.state = State.OPEN;
        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(this.resetTimeout);
                LOGGER.info("timeout elapsed, about to close circuit breaker...");
                this.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    public synchronized void close() {
        LOGGER.info("Circuit breaker closed, resetting failure count");
        this.state = State.CLOSED;
        this.failureCount = 0;
    }
    
    public synchronized boolean isOpen() {
        return this.state == State.OPEN;
    }
    
    public <T> CompletableFuture<T> execute(Supplier<T> supplier) {
        LOGGER.debug("Executing function with circuit breaker");
        if (this.isOpen()) {
            LOGGER.warn("Circuit is open, rejecting request");
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("Circuit is open"));
            return future;
        }
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.debug("Executing function");
            try {
                T result = supplier.get();
                this.resetFailures();
                return result;
            } catch (Exception e) {
                this.recordFailure();
                if (this.failureCount >= this.maxFailures) {
                    this.open();
                }
                throw e;
            }
        });
    }
    
    private synchronized void recordFailure() {
        this.failureCount++;
    }
    
    private synchronized void resetFailures() {
        this.failureCount = 0;
    }
    
    public int getMaxFailures() {
        return this.maxFailures;
    }
    
    public int getTimeout() {
        return this.timeout;
    }
    
    public int getResetTimeout() {
        return this.resetTimeout;
    }
    
    public int getFailureCount() {
        return this.failureCount;
    }
    
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    
    public State getState() {
        return this.state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    public enum State {
        OPEN, CLOSED
    }
    
    public static class Builder {
        private int maxFailures;
        private int timeout;
        private int resetTimeout;
        private State state = State.CLOSED;
        
        public Builder setMaxFailures(int maxFailures) {
            this.maxFailures = maxFailures;
            return this;
        }
        
        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder setResetTimeout(int resetTimeout) {
            this.resetTimeout = resetTimeout;
            return this;
        }
        
        public Builder setState(State state) {
            this.state = state;
            return this;
        }
        
        public CircuitBreaker build() {
            return new CircuitBreaker(this);
        }
    }
}
