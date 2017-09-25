package org.ditto.feature.index.di;


import org.ditto.feature.index.image.FragmentImageIndices;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ImageFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract FragmentImageIndices contributeFragmentImageIndices();


}