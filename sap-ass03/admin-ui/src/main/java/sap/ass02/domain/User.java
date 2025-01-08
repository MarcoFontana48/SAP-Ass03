package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonUtils;
import sap.ddd.Entity;

import java.util.Objects;

/**
 * User entity
 */
public class User implements Entity<UserDTO> {
    private final String id;
    private int credit;
    
    /**
     * Creates a new user
     *
     * @param id the user id
     */
    public User(String id) {
        this.id = id;
        this.credit = 0;
    }
    
    /**
     * @return the user id
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * @return the user credit
     */
    public int getCredit() {
        return this.credit;
    }
    
    /**
     * Recharges the user credit
     *
     * @param deltaCredit the amount to recharge
     */
    public void rechargeCredit(int deltaCredit) {
        this.credit += deltaCredit;
    }
    
    /**
     * Decreases the user credit
     *
     * @param amount the amount to decrease
     */
    public void decreaseCredit(int amount) {
        this.credit -= amount;
        if (this.credit < 0) {
            this.credit = 0;
        }
    }
    
    /**
     * @return the user as a string
     */
    @Override
    public String toString() {
        return "{ id: " + this.id + ", credit: " + this.credit + " }";
    }
    
    /**
     * @return the user as a DTO
     */
    @Override
    public UserDTO toDTO() {
        return new UserDTO(this.id, this.credit);
    }
    
    /**
     * @return the user as a JSON string
     */
    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonFieldKey.USER_ID_KEY, this.id)
                    .put(JsonFieldKey.USER_CREDIT_KEY, this.credit);
        return jsonObject.encode();
    }
    
    /**
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.getCredit() == user.getCredit() && Objects.equals(this.getId(), user.getId());
    }
    
    /**
     * @return the hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getCredit());
    }
    
    @Override
    public JsonObject toJsonObject() {
        return JsonUtils.fromUserToJsonObject(this);
    }
}
