package org.ditto.lib.dbroom.image;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoImage {

    @Insert(onConflict = REPLACE)
    Long save(Buyanswer user);

    @Insert(onConflict = REPLACE)
    Long save(BuyanswerContent content);

    @Insert(onConflict = REPLACE)
    Long save(ImageCommand command);

    @Insert(onConflict = REPLACE)
    Long save(BuyanswerMessage buyanswerMessage);

    @Query("SELECT * FROM buyanswer WHERE uuid = :uuid")
    LiveData<Buyanswer> loadLive(String uuid);

    @Query("SELECT * FROM ImageCommand WHERE type = :type ORDER by created DESC LIMIT :size")
    LiveData<List<ImageCommand>> listCommandsBy(String type, int size);

    @Query("SELECT * FROM ImageCommand WHERE buyanswerUuid = :buyanswerUuid AND type = :type ORDER by created DESC LIMIT :size")
    LiveData<List<ImageCommand>> listCommandsBy(String buyanswerUuid, String type, int size);

    @Query("SELECT * FROM ImageCommand WHERE buyanswerUuid = :buyanswerUuid AND type = :type ORDER by created DESC LIMIT 1")
    LiveData<ImageCommand> findLatestCommandBy(String buyanswerUuid, String type);

    @Query("SELECT * FROM BuyanswerContent WHERE buyanswerUuid = :buyanswerUuid  ORDER by created DESC LIMIT :size")
    LiveData<List<BuyanswerContent>> listContentsBy(String buyanswerUuid, int size);

    @Query("SELECT * FROM Buyanswer ORDER by created DESC LIMIT :size")
    LiveData<List<Buyanswer>> listBuyanswersBy(int size);

    @Query("SELECT * FROM BuyanswerMessage WHERE buyanswerUuid = :buyanswerUuid  ORDER by created DESC LIMIT :size")
    LiveData<List<BuyanswerMessage>> listMessagesBy(String buyanswerUuid, int size);

}