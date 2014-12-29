package io.relayr.model;

import java.io.Serializable;

public class MqttChannel implements Serializable {

    private String channelId;
    private MqttCredentials credentials;

    public MqttChannel(String channelId, MqttCredentials credentials) {
        this.channelId = channelId;
        this.credentials = credentials;
    }

    public String getChannelId() {
        return channelId;
    }

    public MqttCredentials getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "MqttChannel{" +
                "channelId='" + channelId + '\'' +
                ", credentials=" + credentials +
                '}';
    }

    public static class MqttCredentials implements Serializable {
        private String user;
        private String password;
        private String topic;

        public MqttCredentials(String user, String password, String topic) {
            this.user = user;
            this.password = password;
            this.topic = topic;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getTopic() {
            return topic;
        }

        @Override
        public String toString() {
            return "Credentials{" +
                    "user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    ", topic='" + topic + '\'' +
                    '}';
        }
    }
}



