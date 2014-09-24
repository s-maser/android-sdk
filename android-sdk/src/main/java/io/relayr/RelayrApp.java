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

    public static void init(Context context, boolean mockMode) {
        if (sRelayrApp == null) {
            synchronized (new Object()) {
                if (sRelayrApp == null) {
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

    public static void reset() {
        sApplicationContext = null;
        sRelayrApp = null;
        sObjectGraph = null;
    }

}
