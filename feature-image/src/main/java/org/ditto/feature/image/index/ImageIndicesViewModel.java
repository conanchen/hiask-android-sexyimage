package org.ditto.feature.image.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.model.ImageRequest;
import org.ditto.lib.repository.model.LiveDataAndStatus;
import org.ditto.lib.repository.model.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import javax.inject.Inject;

public class ImageIndicesViewModel extends ViewModel {

    private final static String TAG = ImageIndicesViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();


    @VisibleForTesting
    final MutableLiveData<ImageRequest> mutableRequest = new MutableLiveData<>();
    private final LiveData<Pair<PagedList<IndexImage>, Status>> liveImageIndices;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public ImageIndicesViewModel() {
        liveImageIndices = Transformations.switchMap(mutableRequest, login -> {
            if (login == null) {
                return new LiveDataAndStatus<>(AbsentLiveData.create(), AbsentLiveData.create());
            } else {
                return usecaseFascade.repositoryFascade.indexImageRepository
                        .list2ImagesBy(mutableRequest.getValue());
            }
        });
    }

    public LiveData<Pair<PagedList<IndexImage>, Status>> getLiveImageIndices() {
        return this.liveImageIndices;
    }

    public void refresh(ImageRequest imageRequest) {
        Preconditions.checkNotNull(imageRequest);
        this.mutableRequest.setValue(imageRequest);
        Log.i(TAG, String.format("refresh.imageRequest=%s", gson.toJson(imageRequest)));
    }

    public void refresh() {
        ImageRequest imageRequest = this.mutableRequest.getValue();
        if (imageRequest != null) {
            ImageRequest newImageRequest = ImageRequest.builder()
                    .setImageType(imageRequest.imageType)
                    .setPage(imageRequest.page > 0 ? imageRequest.page - 1 : 0)
                    .setPageSize(imageRequest.pageSize)
                    .setRefresh(true)
                    .setLoadMore(false)
                    .build();
            this.mutableRequest.setValue(newImageRequest);
            Log.i(TAG, String.format("refresh.imageRequest=%s", gson.toJson(newImageRequest)));
        }
    }

    public void loadMore() {
        ImageRequest imageRequest = this.mutableRequest.getValue();
        if (imageRequest != null) {
            ImageRequest newImageRequest = ImageRequest.builder()
                    .setImageType(imageRequest.imageType)
                    .setPage(imageRequest.page + 1)
                    .setPageSize(imageRequest.pageSize)
                    .setRefresh(false)
                    .setLoadMore(true)
                    .build();
            this.mutableRequest.setValue(newImageRequest);
            Log.i(TAG, String.format("loadMore.imageRequest=%s", gson.toJson(newImageRequest)));
        }
    }

}