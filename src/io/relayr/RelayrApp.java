package io.relayr;

import android.content.Context;

import dagger.ObjectGraph;

public class RelayrApp {

    private static Context sApplicationContext;
    private static RelayrApp sRelayrApp;
    private static ObjectGraph sObjectGraph;

    private RelayrApp(Context context) {
        sRelayrApp = this;
        sApplicationContext = context.getApplicationContext();
        buildObjectGraphAndInject();
    }

    public static void init(Context context) {
        if (sRelayrApp == null) {
            synchronized (new Object()) {
                if (sRelayrApp == null) {
                    new RelayrApp(context);
                }
            }
        }
    }

    private static void buildObjectGraphAndInject() {
        sObjectGraph = ObjectGraph.create(Modules.list(sApplicationContext));
        sObjectGraph.inject(sRelayrApp);
    }

    public static void inject(Object o) {
        sObjectGraph.inject(o);
    }

    public static Context get() {
        return sApplicationContext;
    }

}
