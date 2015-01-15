package io.relayr.util;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.api.DebugApiModule;
import io.relayr.api.StatusApi;

@Module(
        complete = false,
        library = true,
        includes = DebugApiModule.class
)
public class DebugUtilModule {

    @Provides @Singleton
    ReachabilityUtils provideReachabilityUtils(StatusApi statusApi) {
        return new MockReachabilityUtils(statusApi);
    }
}
