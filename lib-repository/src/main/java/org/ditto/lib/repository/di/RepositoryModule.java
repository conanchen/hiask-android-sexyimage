package org.ditto.lib.repository.di;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.di.ApigrpcModule;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.di.RoomModule;
import org.ditto.lib.repository.IndexImageRepository;
import org.ditto.lib.repository.IndexVisitorRepository;
import org.ditto.lib.repository.RepositoryFascade;
import org.ditto.lib.repository.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module(includes = {
        RoomModule.class,
        ApigrpcModule.class
})
public class RepositoryModule {

    @Singleton
    @Provides
    public RepositoryFascade provideRepositoryFascade(
            UserRepository userRepository,
            IndexImageRepository indexImageRepository,
            IndexVisitorRepository indexVisitorRepository,
            ApigrpcFascade apigrpcFascade
    ) {
        return new RepositoryFascade(
                userRepository,
                indexImageRepository,
                indexVisitorRepository,
                apigrpcFascade);
    }


    @Singleton
    @Provides
    public UserRepository provideUserRepository(
            RoomFascade roomFascade) {
        return new UserRepository( roomFascade);
    }


    @Singleton
    @Provides
    public IndexImageRepository provideMessageIndexRepository(
            ApigrpcFascade apigrpcFascade,
            RoomFascade roomFascade) {
        return new IndexImageRepository(apigrpcFascade, roomFascade);
    }


    @Singleton
    @Provides
    public IndexVisitorRepository provideIndexVisitorRepository(
            RoomFascade roomFascade) {
        return new IndexVisitorRepository(roomFascade);
    }


}