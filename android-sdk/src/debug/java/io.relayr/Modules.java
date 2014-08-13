package io.relayr;

import android.content.Context;

import io.relayr.activity.UiModule;
import io.relayr.api.DebugApiModule;

final class Modules {
    static Object[] list(Context app) {
        return new Object[] {
                new RelayrModule(),
                new DebugApiModule(app),
                new UiModule()
        };
    }

    private Modules() {
        // No instances.
    }
}

