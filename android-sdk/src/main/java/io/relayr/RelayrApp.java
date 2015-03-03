package io.relayr;

import android.content.Context;

import dagger.ObjectGraph;

public class RelayrApp {

    private static Context sApplicationContext;
    private static RelayrApp sRelayrApp;
    private static ObjectGraph sObjectGraph;

    private RelayrApp(Context context, boolean mockMode) {
        sRelayrApp = this;
        sApplicationContext = context.getApplicationContext();
        buildObjectGraphAndInject(mockMode);
    }

    /**
     * Condition (sRelayrApp == null || mockMode) is used when Relayr app is already initialized
     * but you need to recreate it with another set of Dagger modules (e.g. while testing)
     * @param context
     * @param mockMode true for debug mode and tests
     */
    public static void init(Context context, boolean mockMode) {
        reset();
        if (sRelayrApp == null || mockMode) {
            synchronized (new Object()) {
                if (sRelayrApp == null || mockMode) {
                    new RelayrApp(context, mockMode);
                }
            }
        }
    }

    private static void buildObjectGraphAndInject(boolean mockMode) {
        sObjectGraph = mockMode ? ObjectGraph.create(DebugModules.list(sApplicationContext)):
                ObjectGraph.create(Modules.list(sApplicationContext));
        sObjectGraph.injectStatics();
        sObjectGraph.inject(sRelayrApp);
    }

    public static void inject(Object o) {
        sObjectGraph.inject(o);
    }

    public static Context get() {
        return sApplicationContext;
    }

    private static void reset() {
        sApplicationContext = null;
        sRelayrApp = null;
        sObjectGraph = null;
    }

}
