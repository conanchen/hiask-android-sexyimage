package org.ditto.feature.index.di;


import org.ditto.feature.index.image.ImageIndicesViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link ImageViewModelFactory}. Using this component allows
 * ViewModels to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface ImageViewModelSubComponent {


    ImageIndicesViewModel createImageIndicesViewModel();


    @Subcomponent.Builder
    interface Builder {
        ImageViewModelSubComponent build();
    }
}