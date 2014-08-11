package io.relayr;


import dagger.Module;

@Module(
        staticInjections = {
            RelayrSdk.class
        },
        injects = {
                RelayrApp.class
        }, library = true
)
final class RelayrModule { }

