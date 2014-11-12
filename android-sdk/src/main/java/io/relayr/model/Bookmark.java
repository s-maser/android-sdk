package io.relayr.model;

import java.io.Serializable;
import java.util.Date;

public class Bookmark implements Serializable {

    private String userId;
    private String deviceId;
    private Date createdAt;

    public Bookmark(String userId, String deviceId, Date createdAt) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
