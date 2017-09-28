package org.ditto.feature.image.di;

import android.arch.lifecycle.ViewModel;

import org.ditto.feature.base.di.BaseViewModelFactory;
import org.ditto.feature.image.index.ImageIndicesViewModel;
import org.ditto.feature.image.profile.ImageIndexViewModel;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImageViewModelFactory extends BaseViewModelFactory {

    @Inject
    public ImageViewModelFactory(final ImageViewModelSubComponent viewModelSubComponent) {
        super();
        // we cannot inject view models directly because they won't be bound to the owner's
        // view model scope.


        super.creators.put(ImageIndicesViewModel.class,
                () -> viewModelSubComponent.createImageIndicesViewModel());


        super.creators.put(ImageIndexViewModel.class,
                () -> viewModelSubComponent.createImageIndexViewModel());

    }

}