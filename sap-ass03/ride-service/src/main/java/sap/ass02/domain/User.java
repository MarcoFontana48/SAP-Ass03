package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Entity;

import java.util.Objects;

/**
 * Represents a user.
 */
public final class User implements Entity<UserDTO> {
    private final String id;
    private int credit;
    private double xLocation = 0;
    private double yLocation = 0;

    /**
     * Creates a new user with the given id.
     *
     * @param id the id of the user
     */
    public User(String id) {
        this.id = id;
        this.credit = 0;
    }
    
    /**
     * Creates a new user with the given id and credit.
     *
     * @param id the id of the user
     * @param credit the credit of the user
     */
    public User(String id, int credit) {
        this.id = id;
        if (credit < 0) {
            this.credit = 0;
        } else if (credit > 100) {
            this.credit = 100;
        } else {
            this.credit = credit;
        }
    }
    
    /**
     * Creates a new user with the given id, credit, xLocation and yLocation.
     *
     * @param id the id of the user
     * @param credit the credit of the user
     * @param xLocation the x location of the user
     * @param yLocation the y location of the user
     */
    public User(String id, int credit, double xLocation, double yLocation) {
        this(id, credit);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }
    
    /**
     * Creates a new user with the given JsonObject.
     *
     * @param asJsonObject the JsonObject to create the user from
     */
    public User(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.USER_ID_KEY),
                asJsonObject.getInteger(JsonFieldKey.USER_CREDIT_KEY),
                asJsonObject.getDouble(JsonFieldKey.USER_X_LOCATION_KEY),
                asJsonObject.getDouble(JsonFieldKey.USER_Y_LOCATION_KEY)
        );
    }

    /**
     * Returns the id of the user.
     *
     * @return the id of the user
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the credit of the user.
     *
     * @return the credit of the user
     */
    public int getCredit() {
        return this.credit;
    }

    /**
     * Recharges the user's credit by the given amount.
     *
     * @param deltaCredit the amount to recharge
     */
    public void rechargeCredit(int deltaCredit) {
        if (deltaCredit < 0) {
            return;
        }
        if (this.credit + deltaCredit > 100) {
            this.credit = 100;
        } else {
            this.credit += deltaCredit;
        }
    }

    /**
     * Decreases the user's credit by the given amount.
     *
     * @param amount the amount to decrease
     */
    public void decreaseCredit(int amount) {
        if (amount < 0) {
            return;
        }
        this.credit -= amount;
        if (this.credit < 0) {
            this.credit = 0;
        }
    }

    /**
     * Converts the user to a string.
     */
    public String toString() {
        return "{ id: " + this.id + ", credit: " + this.credit + " }";
    }
    
    /**
     * Gets the x location of the user.
     */
    public double getXLocation() {
        return this.xLocation;
    }
    
    /**
     * Gets the y location of the user.
     */
    public double getYLocation() {
        return this.yLocation;
    }
    
    /**
     * Returns a DTO representation of the user.
     */
    @Override
    public UserDTO toDTO() {
        return new UserDTO(this.id, this.credit, this.xLocation, this.yLocation);
    }

    /**
     * Converts the user to a JSON object.
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject
                .put(JsonFieldKey.USER_ID_KEY, this.id)
                .put(JsonFieldKey.USER_CREDIT_KEY, this.credit)
                .put(JsonFieldKey.USER_X_LOCATION_KEY, this.xLocation)
                .put(JsonFieldKey.USER_Y_LOCATION_KEY, this.yLocation);
        return jsonObject;
    }
    
    /**
     * Converts the user to a JSON string.
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }

    /**
     * Checks if the user is equal to another object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.getCredit() == user.getCredit() && Objects.equals(this.getId(), user.getId());
    }

    /**
     * Returns the hash code of the user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getCredit());
    }
}
