package io.relayr;

import android.app.Activity;
import android.content.Context;

import dagger.ObjectGraph;

public class RelayrApp {

    private static Context sApplicationContext;
    private static ObjectGraph sObjectGraph;

    public static void init(Context context) {
        sApplicationContext = context.getApplicationContext();
        buildObjectGraphAndInject();
    }

    private static void buildObjectGraphAndInject() {
        sObjectGraph = ObjectGraph.create(Modules.list(sApplicationContext));
        sObjectGraph.inject(sApplicationContext);
    }

    public static void inject(Object o) {
        sObjectGraph.inject(o);
    }

    public static Context get() {
        return sApplicationContext;
    }

	public static void setCurrentActivity(Activity currentActivity) {

	}

}
