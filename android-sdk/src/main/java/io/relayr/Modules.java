package io.relayr;

import android.content.Context;

import io.relayr.activity.UiModule;
import io.relayr.api.ApiModule;
import io.relayr.ble.BleModule;
import io.relayr.util.UtilModule;
import io.relayr.websocket.WebSocketModule;

final class Modules {
    static Object[] list(Context app) {
        return new Object[] {
                new RelayrModule(),
                new ApiModule(app),
                new WebSocketModule(),
                new BleModule(app),
                new UtilModule(),
                new UiModule()
        };
    }

    private Modules() {
        // No instances.
    }
}

