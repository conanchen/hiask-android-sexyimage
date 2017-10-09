package org.ditto.lib.dbroom.index;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListProvider;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DaoIndexImage {

    @Insert(onConflict = REPLACE)
    Long save(IndexImage indexImage);

    @Insert(onConflict = REPLACE)
    Long[] saveAll(IndexImage... messageIndices);

    @Query("SELECT * FROM IndexImage WHERE type = :imageType ORDER by lastUpdated DESC LIMIT :pageSize")
    LiveData<List<IndexImage>> listImageIndicesBy(String imageType, int pageSize);

    @Query("SELECT * FROM IndexImage WHERE type = :imageType ORDER by lastUpdated DESC")
    public abstract LivePagedListProvider<Integer, IndexImage> listPagingImageIndicesBy(String imageType);

    @Query("SELECT * FROM IndexImage WHERE url = :imageUrl LIMIT 1")
    LiveData<IndexImage> findLive(String imageUrl);

    @Query("SELECT * FROM IndexImage WHERE url = :imageUrl LIMIT 1")
    Flowable<IndexImage> findFlowable(String imageUrl);

    @Query("SELECT * FROM IndexImage WHERE type = :imageType ORDER BY lastUpdated DESC LIMIT 1")
    IndexImage findLatestIndexImage(String imageType);

    @Delete
    void delete(IndexImage indexImage);

}