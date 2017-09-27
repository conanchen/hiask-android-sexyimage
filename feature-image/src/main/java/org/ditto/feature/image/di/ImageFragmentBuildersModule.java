package org.ditto.feature.image.di;


import org.ditto.feature.image.index.FragmentImageIndices;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ImageFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract FragmentImageIndices contributeFragmentImageIndices();

}