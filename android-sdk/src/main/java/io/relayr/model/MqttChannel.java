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
        private String clientId;

        public MqttCredentials(String user, String password, String topic, String clientId) {
            this.user = user;
            this.password = password;
            this.topic = topic;
            this.clientId = clientId;
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

        public String getClientId() {
            return clientId;
        }

        @Override
        public String toString() {
            return "Credentials{" +
                    "user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    ", topic='" + topic + '\'' +
                    ", clientId='" + clientId + '\'' +
                    '}';
        }
    }
}



