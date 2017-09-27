package org.ditto.feature.image.di;


import org.ditto.feature.image.index.ImageIndicesViewModel;

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