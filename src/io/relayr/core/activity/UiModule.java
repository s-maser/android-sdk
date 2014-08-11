package io.relayr.core.activity;

import dagger.Module;

@Module(
        injects = {
                Relayr_LoginActivity.class
        },
        complete = false,
        library = true
)
public class UiModule { }
