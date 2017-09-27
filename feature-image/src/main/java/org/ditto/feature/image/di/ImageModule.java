package org.ditto.feature.image.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = ImageViewModelSubComponent.class)
public   class ImageModule {

    @Singleton
    @Provides
    ImageViewModelFactory provideMessageViewModelFactory(
            ImageViewModelSubComponent.Builder viewModelSubComponent) {
        return new ImageViewModelFactory(viewModelSubComponent.build());
    }

}