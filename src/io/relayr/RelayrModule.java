package io.relayr;


import dagger.Module;

@Module(
        staticInjections = {
                RelayrSdk.class
        },
        includes = {

        },
        injects = {
                RelayrApp.class
        },
        library = true,
        complete = false
)
public final class RelayrModule { }

