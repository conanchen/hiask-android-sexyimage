package org.ditto.lib.dbroom.index;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoIndexImage {

    @Insert(onConflict = REPLACE)
    Long save(IndexImage indexImage);

    @Insert(onConflict = REPLACE)
    Long[] saveAll(IndexImage... messageIndices);

    @Query("SELECT * FROM IndexImage ORDER by lastUpdated DESC LIMIT :size")
    LiveData<List<IndexImage>> listImageIndicesBy(int size);

    @Query("SELECT * FROM IndexImage ORDER by lastUpdated DESC")
    public abstract LivePagedListProvider<Integer , IndexImage> listPagingImageIndicesBy();

    @Query("SELECT * FROM IndexImage WHERE url = :imageUrl LIMIT 1")
    LiveData<IndexImage> find(String imageUrl);
}