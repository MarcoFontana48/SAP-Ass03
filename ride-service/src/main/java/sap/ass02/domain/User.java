package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import sap.ass02.domain.dto.UserDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Entity;

import java.util.Objects;

public final class User implements Entity<UserDTO> {

    private final String id;
    private int credit;

    public User(String id) {
        this.id = id;
        this.credit = 0;
    }
    
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

    public String getId() {
        return this.id;
    }

    public int getCredit() {
        return this.credit;
    }

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

    public void decreaseCredit(int amount) {
        if (amount < 0) {
            return;
        }
        this.credit -= amount;
        if (this.credit < 0) {
            this.credit = 0;
        }
    }

    public String toString() {
        return "{ id: " + this.id + ", credit: " + this.credit + " }";
    }

    @Override
    public UserDTO toDTO() {
        return new UserDTO(this.id, this.credit);
    }

    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(JsonFieldKey.USER_ID_KEY, this.id)
                .put(JsonFieldKey.USER_CREDIT_KEY, this.credit);
        return jsonObject.encode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.getCredit() == user.getCredit() && Objects.equals(this.getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getCredit());
    }
}

