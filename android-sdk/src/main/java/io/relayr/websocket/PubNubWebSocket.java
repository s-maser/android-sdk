package io.relayr.websocket;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import io.relayr.model.MqttChannel;
import io.relayr.model.WebSocketConfig;

/**
 * It facilitates the subscription to channels in order to get the readings from the devices.
 */
class PubNubWebSocket extends WebSocket<String> {

    private final Pubnub mPubnub;

    /**
     * The authKey and cipherKey won't change while there is at least one sensor publishing data.
     */
    PubNubWebSocket(WebSocketConfig webSocketConfig) {
        mPubnub = new Pubnub(webSocketConfig.subscribeKey, webSocketConfig.subscribeKey,
                webSocketConfig.authKey, webSocketConfig.cipherKey, true);
        mPubnub.setAuthKey(webSocketConfig.authKey);
    }

    boolean isSubscribedToAnyone() {
        return mPubnub.getSubscribedChannelsArray().length == 0;
    }

    /**
     * Subscribe to a device in order to get it's readings
     */
    void subscribe(String channel, final WebSocketCallback webSocketCallback) {
        if (webSocketCallback == null) return;
        try {
            mPubnub.subscribe(channel, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    webSocketCallback.connectCallback(message.toString().replace("\\", ""));
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    webSocketCallback.disconnectCallback(message.toString().replace("\\", ""));
                }

                public void reconnectCallback(String channel, Object message) {
                    webSocketCallback.reconnectCallback(message.toString().replace("\\", ""));
                }

                @Override
                public void successCallback(String channel, Object message) {
                    webSocketCallback.successCallback(message.toString().replace("\\", ""));
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    webSocketCallback.errorCallback(new Throwable(error.toString()));
                }
            });
        } catch (PubnubException e) { /* Never happens: Thrown when the callback is null */ }
    }

    @Override
    void createClient(String clientId) {

    }

    /**
     * Stop getting notifications from a specific sensor.
     * @param channel
     */
    boolean unSubscribe(MqttChannel channel) {
        mPubnub.unsubscribe("");
        return false;
    }

    /**
     * Stop getting notifications from all the sensors.
     */
    void unSubscribeAll() {
        mPubnub.unsubscribeAll();
    }
}
