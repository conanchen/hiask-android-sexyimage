package org.ditto.lib.dbroom;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.TypeConverters;

import org.ditto.lib.dbroom.index.DaoIndexImage;
import org.ditto.lib.dbroom.index.DaoIndexVisitor;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.dbroom.index.IndexVisitor;
import org.ditto.lib.dbroom.user.ConverterUser;
import org.ditto.lib.dbroom.user.DaoUser;
import org.ditto.lib.dbroom.user.Myprofile;
import org.ditto.lib.dbroom.user.User;
import org.ditto.lib.dbroom.user.UserCommand;


@Database(entities =
        {
                Myprofile.class,
                User.class,
                UserCommand.class,
                IndexImage.class,
                IndexVisitor.class
        }, version = 2)
@TypeConverters({Converters.class,  ConverterUser.class})
public abstract class MyRoomDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract DaoUser daoUser();


    public abstract DaoIndexImage daoMessageIndex();

    public abstract DaoIndexVisitor daoPartyIndex();

}