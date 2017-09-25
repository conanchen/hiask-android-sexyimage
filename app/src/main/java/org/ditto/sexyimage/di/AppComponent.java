package org.ditto.sexyimage.di;

import android.app.Application;

import org.ditto.feature.index.di.ImageModule;
import org.ditto.feature.visitor.di.VisitorModule;
import org.ditto.sexyimage.SexyApplication;
import org.ditto.lib.usecases.di.UsecaseModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,

        AppModule.class,

        MainActivityModule.class,
        ImageModule.class,
        VisitorModule.class,

        UsecaseModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(SexyApplication githubApp);
}