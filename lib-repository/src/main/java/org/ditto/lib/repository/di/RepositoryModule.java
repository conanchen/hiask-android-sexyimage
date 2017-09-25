package org.ditto.lib.repository.di;

import org.ditto.lib.apigrpc.ApigrpcFascade;
import org.ditto.lib.apigrpc.di.ApigrpcModule;
import org.ditto.lib.apirest.ApirestFascade;
import org.ditto.lib.apirest.di.ApirestModule;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.di.RoomModule;
import org.ditto.lib.repository.BuyanswerRepository;
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
        ApigrpcModule.class,
        ApirestModule.class
})
public class RepositoryModule {

    @Singleton
    @Provides
    public RepositoryFascade provideRepositoryFascade(
            UserRepository userRepository,
            BuyanswerRepository buyanswerRepository,
            IndexImageRepository indexImageRepository,
            IndexVisitorRepository indexVisitorRepository,
            ApigrpcFascade apigrpcFascade,
            ApirestFascade apirestFascade
    ) {
        return new RepositoryFascade(userRepository,
                buyanswerRepository,
                indexImageRepository,
                indexVisitorRepository,
                apigrpcFascade,
                apirestFascade);
    }


    @Singleton
    @Provides
    public UserRepository provideUserRepository(
            ApirestFascade apirestFascade,
            RoomFascade roomFascade) {
        return new UserRepository(apirestFascade, roomFascade);
    }


    @Singleton
    @Provides
    public BuyanswerRepository provideBuyanswerRepository(
            ApirestFascade apirestFascade,
            RoomFascade roomFascade) {
        return new BuyanswerRepository(apirestFascade, roomFascade);
    }


    @Singleton
    @Provides
    public IndexImageRepository provideMessageIndexRepository(
            ApirestFascade apirestFascade,
            ApigrpcFascade apigrpcFascade,
            RoomFascade roomFascade) {
        return new IndexImageRepository(apirestFascade, apigrpcFascade,roomFascade);
    }


    @Singleton
    @Provides
    public IndexVisitorRepository provideIndexVisitorRepository(
            ApirestFascade apirestFascade,
            RoomFascade roomFascade) {
        return new IndexVisitorRepository(apirestFascade, roomFascade);
    }


}