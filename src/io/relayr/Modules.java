package io.relayr;

import android.content.Context;

import io.relayr.core.activity.UiModule;
import io.relayr.core.api.ApiModule;

final class Modules {
    static Object[] list(Context app) {
        return new Object[] {
                new ApiModule(app),
                new UiModule()
        };
    }

    private Modules() {
        // No instances.
    }
}

