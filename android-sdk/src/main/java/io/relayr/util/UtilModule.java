package io.relayr.util;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.api.ApiModule;
import io.relayr.api.StatusApi;
import io.relayr.model.Status;
import retrofit.Endpoint;
import retrofit.Endpoints;

@Module(
        complete = false,
        library = true,
        includes = ApiModule.class
)
public class UtilModule {

    @Provides @Singleton
    ReachabilityUtils provideReachabilityUtils(StatusApi statusApi) {
        return new ReachabilityUtils(statusApi);
    }
}
