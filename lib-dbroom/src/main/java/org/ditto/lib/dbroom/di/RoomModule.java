package org.ditto.lib.dbroom.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.ditto.lib.dbroom.index.DaoIndexImage;
import org.ditto.lib.dbroom.MyRoomDatabase;
import org.ditto.lib.dbroom.RoomFascade;
import org.ditto.lib.dbroom.index.DaoIndexVisitor;
import org.ditto.lib.dbroom.user.DaoUser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amirziarati on 10/4/16.
 */
@Singleton
@Module
public class RoomModule {

    @Singleton
    @Provides
    public String provideAmir() {
        return "Amir Was Injected";
    }

    @Singleton
    @Provides
    MyRoomDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, MyRoomDatabase.class, "room.db").build();
    }

    @Singleton
    @Provides
    DaoUser provideUserDao(MyRoomDatabase db) {
        return db.daoUser();
    }



    @Singleton
    @Provides
    DaoIndexImage provideMessageDao(MyRoomDatabase db) {
        return db.daoMessageIndex();
    }

    @Singleton
    @Provides
    DaoIndexVisitor provideDaoPartyIndex(MyRoomDatabase db) {
        return db.daoPartyIndex();
    }



    @Singleton
    @Provides
    public RoomFascade provideRoomFascade(DaoUser daoUser,
                                          DaoIndexImage daoIndexImage,
                                          DaoIndexVisitor daoIndexVisitor
    ) {
        return new RoomFascade(daoUser, daoIndexImage, daoIndexVisitor);
    }
}