package org.ditto.feature.image.index;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;

import org.ditto.lib.AbsentLiveData;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.LiveDataAndStatus;
import org.ditto.lib.repository.util.Status;
import org.ditto.lib.usecases.UsecaseFascade;

import java.util.List;

import javax.inject.Inject;

public class ImageIndicesViewModel extends ViewModel {
    @VisibleForTesting
    final MutableLiveData<String> mutableLogin = new MutableLiveData<>();
    private final LiveData<Pair<List<IndexImage>, Status>> liveImageIndices;


    @Inject
    UsecaseFascade usecaseFascade;

    @SuppressWarnings("unchecked")
    @Inject
    public ImageIndicesViewModel() {
        liveImageIndices = Transformations.switchMap(mutableLogin, login -> {
            if (login == null) {
                return new LiveDataAndStatus<>(AbsentLiveData.create(), AbsentLiveData.create());
            } else {
                return usecaseFascade.repositoryFascade.indexImageRepository.list2ImagesBy(20);
            }
        });
    }

    public LiveData<Pair<List<IndexImage>, Status>> getLiveImageIndices() {
        return this.liveImageIndices;
    }

    public void refresh() {
        this.mutableLogin.setValue("GO");
    }

}