package org.ditto.feature.image.index;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.common.base.Strings;

import org.ditto.feature.base.epoxymodels.ItemStatusNetworkModel_;
import org.ditto.feature.image.index.epoxymodels.ItemImageModel_;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.Status;

import java.util.List;

public class ImageIndicesController extends TypedEpoxyController<Pair<List<IndexImage>, Status>> {
    public interface AdapterCallbacks {
        void onMessageItemClicked(IndexImage indexImageIssue, int position);
    }


    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;

    public ImageIndicesController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @AutoModel
    ItemStatusNetworkModel_ itemStatusNetworkModel_;

    @Override
    protected void buildModels(Pair<List<IndexImage>, Status> dataNstatus) {
        List<IndexImage> messageIndices = dataNstatus.first;
        Status status = dataNstatus.second;

        if (status != null) {
            itemStatusNetworkModel_.addTo(this);
        }

        if (messageIndices != null) {
            for (IndexImage indexImage : messageIndices) {
                if (indexImage != null && !Strings.isNullOrEmpty(indexImage.url)) {
                    add(new ItemImageModel_()
                            .id(indexImage.url)
                            .url(indexImage.url)
                            .toprank(indexImage.toprank)
                            .clickListener((model, parentView, clickedView, position) -> {
                                // A model click listener is used instead of a normal click listener so that we can get
                                // the current position of the view. Since the view may have been moved when the colors
                                // were shuffled we can't rely on the position of the model when it was added here to
                                // be correct, since the model won't have been rebound when shuffled.
                                callbacks.onMessageItemClicked(indexImage, position);
                            })
                    );
                }
            }
        }
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
