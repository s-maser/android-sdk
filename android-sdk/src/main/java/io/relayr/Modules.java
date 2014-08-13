package io.relayr;

import android.content.Context;

import io.relayr.activity.UiModule;
import io.relayr.api.ApiModule;

final class Modules {
    static Object[] list(Context app) {
        return new Object[] {
                new RelayrModule(),
                new ApiModule(app),
                new UiModule()
        };
    }

    private Modules() {
        // No instances.
    }
}

