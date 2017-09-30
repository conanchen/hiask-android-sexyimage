package org.ditto.feature.image.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.gson.Gson;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.LiveDataAndStatus;
import org.ditto.lib.repository.util.Status;
import org.ditto.lib.usecases.UsecaseFascade;
import org.ditto.sexyimage.grpc.Common;

import javax.inject.Inject;

public class ImageIndicesViewModel extends ViewModel {

    private final static String TAG = ImageIndicesViewModel.class.getSimpleName();
    private final static Gson gson = new Gson();

    static public class Request {
        Common.ImageType imageType;
        int page;

        public Request(Common.ImageType imageType, int page) {
            this.imageType = imageType;
            this.page = page;
        }
    }

    @VisibleForTesting
    final MutableLiveData<Request> mutableRequest = new MutableLiveData<>();
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
                return usecaseFascade.repositoryFascade.indexImageRepository.list2ImagesBy(mutableRequest.getValue().imageType, mutableRequest.getValue().page, 25);
            }
        });
    }

    public LiveData<Pair<PagedList<IndexImage>, Status>> getLiveImageIndices() {
        return this.liveImageIndices;
    }

    public void refresh(Request request) {
        this.mutableRequest.setValue(request);
    }

    public void refresh() {
        Request request = this.mutableRequest.getValue();
        if (request != null) {
            request.page = request.page > 0 ? request.page - 1 : 0;
            this.mutableRequest.setValue(request);
            Log.i(TAG,String.format("refresh.request=%s",gson.toJson(request)));
        }
    }

    public void loadMore() {
        Request request = this.mutableRequest.getValue();
        if (request != null) {
            request.page += 1;
            this.mutableRequest.setValue(request);
            Log.i(TAG,String.format("loadMore.request=%s",gson.toJson(request)));
        }

    }
}