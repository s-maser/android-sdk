package io.relayr.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import javax.inject.Inject;

import io.relayr.TestEnvironment;
import io.relayr.model.MqttChannel;
import io.relayr.model.MqttDefinition;
import rx.Observer;

import static org.fest.assertions.api.Assertions.assertThat;

public class MockChannelApiTest extends TestEnvironment {

    @Inject ChannelApi channelApi;

    @Captor private ArgumentCaptor<MqttChannel> statusCaptor;

    @Mock private Observer subscriber;

    private MqttChannel createdChannel;

    @Before
    public void init() {
        super.init();
        inject();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getMqttData() throws Exception {
        channelApi.create(new MqttDefinition("shiny_id", "dev_id"))
                .subscribe(new Observer<MqttChannel>() {
                    @Override
                    public void onCompleted() {
                        countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        countDown();
                    }

                    @Override
                    public void onNext(MqttChannel mqttChannel) {
                        createdChannel = mqttChannel;
                        countDown();
                    }
                });

        await();

        assertThat(createdChannel).isNotNull();
        assertThat(createdChannel.getChannelId()).isEqualTo("0bfc0cd2-3952-4a61-9511-59b360a19ccf");
    }
}
