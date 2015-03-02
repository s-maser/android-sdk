package io.relayr.model;

import java.io.Serializable;
import java.util.List;

public class MqttExistingChannel implements Serializable {

    private String deviceId;
    private List<MqttChannelInfo> channels;

    public MqttExistingChannel(String deviceId, List<MqttChannelInfo> channels) {
        this.deviceId = deviceId;
        this.channels = channels;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public List<MqttChannelInfo> getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "MqttExistingChannel{" +
                "deviceId='" + deviceId + '\'' +
                ", channels=" + channels +
                '}';
    }

    public class MqttChannelInfo {
        String channelId;
        String appId;
        String transport;

        public MqttChannelInfo(String channelId, String appId, String transport) {
            this.channelId = channelId;
            this.appId = appId;
            this.transport = transport;
        }

        @Override
        public String toString() {
            return "MqttChannelInfo{" +
                    "channelId='" + channelId + '\'' +
                    ", appId='" + appId + '\'' +
                    ", transport='" + transport + '\'' +
                    '}';
        }
    }

}



