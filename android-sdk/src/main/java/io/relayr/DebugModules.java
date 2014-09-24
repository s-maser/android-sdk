package io.relayr;

import android.content.Context;

import io.relayr.activity.UiModule;
import io.relayr.api.DebugApiModule;
import io.relayr.ble.DebugBleModule;
import io.relayr.websocket.DebugWebSocketModule;

final class DebugModules {
    static Object[] list(Context app) {
        return new Object[] {
                new RelayrModule(),
                new DebugApiModule(app),
                new DebugWebSocketModule(),
                new DebugBleModule(app),
                new UiModule()
        };
    }

    private DebugModules() {
        // No instances.
    }
}

