package org.ditto.feature.image.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Strings;

import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.usecases.UsecaseFascade;
import org.ditto.sexyimage.grpc.Common;

import javax.inject.Inject;

public class ImageIndexViewModel extends ViewModel {
    private final static String TAG = ImageIndexViewModel.class.getSimpleName();
    @VisibleForTesting
    final MutableLiveData<String> mutableUrl = new MutableLiveData<String>();
    private final LiveData<IndexImage> liveImageIndexForUpsert;

    public LiveData<IndexImage> getLiveImageIndexForUpsert() {
        return liveImageIndexForUpsert;
    }

    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public ImageIndexViewModel() {
        liveImageIndexForUpsert = Transformations.switchMap(mutableUrl, (String url) -> {
            LiveData<IndexImage> result = null;
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

        usecaseFascade.repositoryFascade.indexImageRepository.upsert(liveImageIndexForUpsert.getValue());
    }

    public void delete() {
        if (isUpdate()) {
            usecaseFascade.repositoryFascade.indexImageRepository.delete(mutableUrl.getValue());
        }
    }
}