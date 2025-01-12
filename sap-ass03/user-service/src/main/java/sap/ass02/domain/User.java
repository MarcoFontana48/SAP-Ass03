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
     * Constructor.
     *
     * @param id the id
     */
    public User(String id) {
        this.id = id;
        this.credit = 0;
    }
    
    /**
     * Constructor.
     *
     * @param id the id
     * @param credit the credit
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
     * Constructor.
     *
     * @param id the id
     * @param credit the credit
     * @param xLocation the x location
     * @param yLocation the y location
     */
    public User(String id, int credit, double xLocation, double yLocation) {
        this(id, credit);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }
    
    /**
     * Constructor.
     *
     * @param asJsonObject the as json object
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
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the credit.
     *
     * @return the credit
     */
    public int getCredit() {
        return this.credit;
    }

    /**
     * Recharges the credit.
     *
     * @param deltaCredit the delta credit
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
     * Decreases the credit.
     *
     * @param amount the amount
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
     * Gets the x location.
     */
    public double getXLocation() {
        return this.xLocation;
    }
    
    /**
     * Gets the y location.
     */
    public double getYLocation() {
        return this.yLocation;
    }
    
    /**
     * converts the user to a DTO.
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
     * Compares the user to another object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.getCredit() == user.getCredit() && Objects.equals(this.getId(), user.getId());
    }

    /**
     * Gets the hash code of the user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getCredit());
    }
}

