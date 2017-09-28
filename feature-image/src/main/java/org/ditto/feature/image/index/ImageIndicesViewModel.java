package org.ditto.feature.image.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.VisibleForTesting;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.usecases.UsecaseFascade;

import java.util.List;

import javax.inject.Inject;

public class ImageIndicesViewModel extends ViewModel {
    @VisibleForTesting
    final MutableLiveData<String> mutableLogin = new MutableLiveData<>();
    private final LiveData<List<IndexImage>> liveImageIndices;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public ImageIndicesViewModel() {
        liveImageIndices = Transformations.switchMap(mutableLogin, login -> {
            if (login == null) {
                return AbsentLiveData.create();
            } else {
                return usecaseFascade.repositoryFascade.indexImageRepository.listImagesBy(20);
            }
        });
    }


    void retry() {
        if (this.mutableLogin.getValue() != null) {
            this.mutableLogin.setValue(this.mutableLogin.getValue());
        }
    }

    public LiveData<List<IndexImage>> getLiveImageIndices() {
        return this.liveImageIndices;
    }

    public void refresh() {
        this.mutableLogin.setValue("GO");
    }

}