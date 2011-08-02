package com.example.android.sampleapp;

import com.google.inject.Module;
import roboguice.application.RoboInjectableApplication;
import roboguice.config.AbstractAndroidModule;

import java.util.List;

public class MySampleApplication extends RoboInjectableApplication {
    private Module module = new ApplicationModule();

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(module);
    }

    /* just for test injection */
    public void setModule(Module module) {
        this.module = module;
    }

    public static class ApplicationModule extends AbstractAndroidModule {
        @Override
        protected void configure() {
            /*Samples of injection binding*/
            //        bind(FooBar.class).in(Scopes.SINGLETON);
            //        bind(Date.class).toProvider(FakeDateProvider.class);
            //        bind(Ln.BaseConfig.class).toInstance(new SampleLoggerConfig());
        }
    }
}
