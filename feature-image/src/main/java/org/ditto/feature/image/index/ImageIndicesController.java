package org.ditto.feature.image.index;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.TypedEpoxyController;

import org.ditto.feature.image.index.epoxymodels.ItemImageModel_;
import org.ditto.lib.dbroom.index.IndexImage;

import java.util.List;

public class ImageIndicesController extends TypedEpoxyController<List<IndexImage>> {
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

    @Override
    protected void buildModels(List<IndexImage> messageIndices) {
        if (messageIndices != null) {
            for (IndexImage carousel : messageIndices) {
                add(new ItemImageModel_()
                        .id(carousel.url)
                        .url(carousel.url)
                        .toprank(carousel.toprank)
                        .clickListener((model, parentView, clickedView, position) -> {
                            // A model click listener is used instead of a normal click listener so that we can get
                            // the current position of the view. Since the view may have been moved when the colors
                            // were shuffled we can't rely on the position of the model when it was added here to
                            // be correct, since the model won't have been rebound when shuffled.
                            callbacks.onMessageItemClicked(carousel, position);
                        })
                );

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
