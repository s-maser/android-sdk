package io.relayr.model;

import java.io.Serializable;
import java.util.Date;

public class Bookmark implements Serializable {

    /** Auto generated uid */
    private static final long serialVersionUID = 1L;
    private final String userId;
    private final String deviceId;
    private final Date createdAt;

    public Bookmark(String userId, String deviceId, Date createdAt) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Date getCreatedAt() {
        return createdAt;
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
