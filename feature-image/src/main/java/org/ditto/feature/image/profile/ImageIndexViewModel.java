package org.ditto.feature.image.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Strings;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.Status;
import org.ditto.lib.system.di.SystemModule;
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

    private final LiveData<IndexImage> liveImageIndexForUpsert;
    private final LiveData<Status> liveUpsertStatus;
    private final LiveData<Status> liveDeleteStatus;

    public LiveData<IndexImage> getLiveImageIndexForUpsert() {
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
            LiveData<IndexImage> result = AbsentLiveData.create();
            ;
            if (!Strings.isNullOrEmpty(url)) {
                result = usecaseFascade.repositoryFascade.indexImageRepository.find(url);
            }

            if (result == null) {
                result = new LiveData<IndexImage>() {
                    @Override
                    protected void onActive() {
                        update = false;
                        postValue(IndexImage
                                .builder()
                                .setUrl("http://")
                                .setInfoUrl("http://")
                                .setTitle("")
                                .setType(Common.ImageType.SECRET.name())
                                .setCreated(System.currentTimeMillis())
                                .setLastUpdated(System.currentTimeMillis())
                                .setActive(false)
                                .setToprank(false)
                                .build()
                        );
                    }
                };
            } else {
                update = true;
            }

            return result;

        });
        liveUpsertStatus = Transformations.switchMap(mutableUpsert, (Long time) -> {
              return usecaseFascade.repositoryFascade.indexImageRepository.upsert(liveImageIndexForUpsert.getValue());
        });
        liveDeleteStatus = Transformations.switchMap(mutableDelete,(Long time)->{
            return   usecaseFascade.repositoryFascade.indexImageRepository.delete(mutableUrl.getValue());

        });
    }

    private boolean update;


    public ImageIndexViewModel setNewUrl(String newUrl) {
        liveImageIndexForUpsert.getValue().url = newUrl;
        return this;
    }

    public ImageIndexViewModel setNewInfoUrl(String newInfoUrl) {
        liveImageIndexForUpsert.getValue().infoUrl = newInfoUrl;
        return this;
    }

    public ImageIndexViewModel setNewTitle(String newTitle) {
        liveImageIndexForUpsert.getValue().title = newTitle;
        return this;
    }

    public ImageIndexViewModel setNewType(Common.ImageType newType) {
        liveImageIndexForUpsert.getValue().type = newType.name();
        return this;
    }

    public ImageIndexViewModel setNewActive(boolean newActive) {
        liveImageIndexForUpsert.getValue().active = newActive;
        return this;
    }

    public ImageIndexViewModel setNewToprank(boolean newToprank) {
        liveImageIndexForUpsert.getValue().toprank = newToprank;
        return this;
    }

    public boolean isUpdate() {
        return update;
    }


    public void findForUpsert(String imageUrl) {
        mutableUrl.setValue(imageUrl);
    }

    public void saveUpdates() {
        mutableUpsert.setValue(System.currentTimeMillis());
    }

    public void delete() {
        if (isUpdate()) {
            mutableDelete.setValue(System.currentTimeMillis());
        }
    }
}