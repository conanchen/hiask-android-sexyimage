package org.ditto.sexyimage.di;

import org.ditto.feature.image.profile.UpsertActivity;
import org.ditto.feature.image.di.ImageFragmentBuildersModule;
import org.ditto.feature.visitor.di.VisitorFragmentBuildersModule;
import org.ditto.sexyimage.MainActivity;
import org.ditto.lib.usecases.AppServiceCommandSenderImpl;
import org.ditto.lib.usecases.AppServiceKeepliveTraceImpl;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module()
public abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = {
            ImageFragmentBuildersModule.class,
            VisitorFragmentBuildersModule.class})

    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract UpsertActivity contributeUpsertActivity();

    @ContributesAndroidInjector
    abstract AppServiceKeepliveTraceImpl contributeAppServiceKeepliveTraceImpl();

    @ContributesAndroidInjector
    abstract AppServiceCommandSenderImpl contributeAppServiceCommandSenderImpl();
}