package sap.ass02.infrastructure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Circuit breaker implementation
 */
public final class CircuitBreaker {
    private static final Logger LOGGER = LogManager.getLogger(CircuitBreaker.class);
    private final int maxFailures;
    private final int timeout;
    private final int resetTimeout;
    private int failureCount = 0;
    private State state = State.CLOSED;
    
    /**
     * Constructor
     *
     * @param builder the builder
     */
    private CircuitBreaker(Builder builder) {
        this.maxFailures = builder.maxFailures;
        this.timeout = builder.timeout;
        this.resetTimeout = builder.resetTimeout;
        this.state = builder.state;
    }
    
    /**
     * opens the circuit breaker
     */
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
    
    /**
     * closes the circuit breaker
     */
    public synchronized void close() {
        LOGGER.info("Circuit breaker closed, resetting failure count");
        this.state = State.CLOSED;
        this.failureCount = 0;
    }
    
    /**
     * checks if the circuit breaker is open
     *
     * @return true if the circuit breaker is open, false otherwise
     */
    public synchronized boolean isOpen() {
        return this.state == State.OPEN;
    }
    
    /**
     * executes a function with the circuit breaker
     *
     * @param supplier the function to execute
     * @param <T>      the return type of the function
     * @return a CompletableFuture with the result of the function
     */
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
    
    /**
     * records a failure
     */
    private synchronized void recordFailure() {
        this.failureCount++;
    }
    
    /**
     * resets the failure count
     */
    private synchronized void resetFailures() {
        this.failureCount = 0;
    }
    
    /**
     * gets the maximum number of failures
     *
     * @return the maximum number of failures
     */
    public int getMaxFailures() {
        return this.maxFailures;
    }
    
    /**
     * gets the timeout
     *
     * @return the timeout
     */
    public int getTimeout() {
        return this.timeout;
    }
    
    /**
     * gets the reset timeout
     *
     * @return the reset timeout
     */
    public int getResetTimeout() {
        return this.resetTimeout;
    }
    
    /**
     * gets the failure count
     *
     * @return the failure count
     */
    public int getFailureCount() {
        return this.failureCount;
    }
    
    /**
     * sets the failure count
     *
     * @param failureCount the failure count
     */
    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
    
    /**
     * gets the state
     *
     * @return the state
     */
    public State getState() {
        return this.state;
    }
    
    /**
     * sets the state
     *
     * @param state the state
     */
    public void setState(State state) {
        this.state = state;
    }
    
    /**
     * gets the builder
     *
     * @return the builder
     */
    public enum State {
        OPEN, CLOSED
    }
    
    /**
     * Builder class
     */
    public static class Builder {
        private int maxFailures;
        private int timeout;
        private int resetTimeout;
        private State state = State.CLOSED;
        
        /**
         * sets the maximum number of failures
         */
        public Builder setMaxFailures(int maxFailures) {
            this.maxFailures = maxFailures;
            return this;
        }
        
        /**
         * sets the timeout
         */
        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        
        /**
         * sets the reset timeout
         */
        public Builder setResetTimeout(int resetTimeout) {
            this.resetTimeout = resetTimeout;
            return this;
        }
        
        /**
         * sets the state
         */
        public Builder setState(State state) {
            this.state = state;
            return this;
        }
        
        /**
         * builds the circuit breaker
         */
        public CircuitBreaker build() {
            return new CircuitBreaker(this);
        }
    }
}
