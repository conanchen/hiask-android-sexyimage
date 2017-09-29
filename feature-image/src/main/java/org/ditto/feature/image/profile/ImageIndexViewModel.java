package org.ditto.feature.image.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;

import com.google.common.base.Strings;

import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.Status;
import org.ditto.lib.usecases.UsecaseFascade;
import org.ditto.sexyimage.grpc.Common;

import javax.inject.Inject;

public class ImageIndexViewModel extends ViewModel {
    private final static String TAG = ImageIndexViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<String> mutableUrl = new MutableLiveData<String>();
    @VisibleForTesting
    final MutableLiveData<Long> mutableUpsert = new MutableLiveData<Long>();
    @VisibleForTesting
    final MutableLiveData<Long> mutableDelete = new MutableLiveData<Long>();

    private final LiveData<Pair<IndexImage, Boolean>> liveImageIndexForUpsert;
    private final LiveData<Status> liveUpsertStatus;
    private final LiveData<Status> liveDeleteStatus;

    public LiveData<Pair<IndexImage, Boolean>> getLiveImageIndexForUpsert() {
        return liveImageIndexForUpsert;
    }

    public LiveData<Status> getLiveUpsertStatus() {
        return liveUpsertStatus;
    }

    public LiveData<Status> getLiveDeleteStatus() {
        return liveDeleteStatus;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public ImageIndexViewModel() {
        liveImageIndexForUpsert = Transformations.switchMap(mutableUrl, (String url) -> {
            LiveData<IndexImage> liveData = usecaseFascade.repositoryFascade.indexImageRepository.find(url);

            return new MediatorLiveData<Pair<IndexImage, Boolean>>() {
                {
                    addSource(liveData, indexImage -> {
                        if (indexImage == null) {
                            indexImage = IndexImage
                                    .builder()
                                    .setUrl(Strings.isNullOrEmpty(url) ? "http://" : url)
                                    .setInfoUrl("http://")
                                    .setTitle("")
                                    .setType(Common.ImageType.SECRET.name())
                                    .setCreated(System.currentTimeMillis())
                                    .setLastUpdated(System.currentTimeMillis())
                                    .setActive(false)
                                    .setToprank(false)
                                    .build();
                            setValue(new Pair<>(indexImage, Boolean.FALSE));
                        } else {
                            setValue(new Pair<>(indexImage, Boolean.TRUE));
                        }
                    });
                }
            };
        });
        liveUpsertStatus = Transformations.switchMap(mutableUpsert, (Long time) -> {
            return usecaseFascade.repositoryFascade.indexImageRepository.upsert(liveImageIndexForUpsert.getValue().first);
        });
        liveDeleteStatus = Transformations.switchMap(mutableDelete, (Long time) -> {
            return usecaseFascade.repositoryFascade.indexImageRepository.delete(mutableUrl.getValue());

        });
    }


    public ImageIndexViewModel setNewUrl(String newUrl) {
        liveImageIndexForUpsert.getValue().first.url = newUrl;
        return this;
    }

    public ImageIndexViewModel setNewInfoUrl(String newInfoUrl) {
        liveImageIndexForUpsert.getValue().first.infoUrl = newInfoUrl;
        return this;
    }

    public ImageIndexViewModel setNewTitle(String newTitle) {
        liveImageIndexForUpsert.getValue().first.title = newTitle;
        return this;
    }

    public ImageIndexViewModel setNewType(Common.ImageType newType) {
        liveImageIndexForUpsert.getValue().first.type = newType.name();
        return this;
    }

    public ImageIndexViewModel setNewActive(boolean newActive) {
        liveImageIndexForUpsert.getValue().first.active = newActive;
        return this;
    }

    public ImageIndexViewModel setNewToprank(boolean newToprank) {
        liveImageIndexForUpsert.getValue().first.toprank = newToprank;
        return this;
    }


    public void findForUpsert(String imageUrl) {
        mutableUrl.setValue(imageUrl);
    }

    public void saveUpdates() {
        mutableUpsert.setValue(System.currentTimeMillis());
    }

    public void delete() {
        mutableDelete.setValue(System.currentTimeMillis());
    }
}