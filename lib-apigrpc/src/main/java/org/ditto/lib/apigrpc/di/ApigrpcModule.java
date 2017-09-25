package org.ditto.lib.apigrpc.di;

import org.ditto.lib.apigrpc.BuildConfig;
import org.ditto.lib.apigrpc.ImageManService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.grpc.ManagedChannel;
import io.grpc.okhttp.OkHttpChannelBuilder;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module
public class ApigrpcModule {



    @Singleton
    @Provides
    ManagedChannel provideManagedChannel() {
        final ManagedChannel channel = OkHttpChannelBuilder
                .forAddress(BuildConfig.GRPC_SERVER_HOST, BuildConfig.GRPC_SERVER_PORT)
                .usePlaintext(true)
                .keepAliveTime(60, TimeUnit.SECONDS)
                .build();
        return channel;
    }

    @Singleton
    @Provides
    ImageManService provideGithubService(final ManagedChannel channel) {
        return new ImageManService(channel);
    }

}