package org.ditto.feature.image.index;

import android.arch.paging.PagedList;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.util.Log;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.ditto.feature.base.epoxymodels.ItemLoadmoreFooterModel_;
import org.ditto.feature.base.epoxymodels.ItemRefreshHeaderModel_;
import org.ditto.feature.base.epoxymodels.ItemStatusNetworkModel_;
import org.ditto.feature.image.index.epoxymodels.ItemImageModel_;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.model.Status;

public class ImageIndicesController extends TypedEpoxyController<Pair<PagedList<IndexImage>, Status>> {
    private final static String TAG = ImageIndicesController.class.getSimpleName();
    private final static Gson gson = new Gson();

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
    ItemRefreshHeaderModel_ headerModel_;

    @AutoModel
    ItemStatusNetworkModel_ itemStatusNetworkModel_;

    @AutoModel
    ItemLoadmoreFooterModel_ footerModel_;

    @Override
    protected void buildModels(Pair<PagedList<IndexImage>, Status> dataNstatus) {

        PagedList<IndexImage> messageIndices = dataNstatus.first;
        Status status = dataNstatus.second;

        Log.i(TAG, String.format(" status!=null && status.refresh = %b", status != null && status.refresh));
        headerModel_.status(status).addIf(status != null && status.refresh, this);

        itemStatusNetworkModel_.addIf(status != null && Status.Code.END_ERROR.equals(status.code), this);

        if (messageIndices != null) {
            Log.i(TAG, String.format(" build %d IndexImage", messageIndices.size()));
            for (IndexImage indexImage : messageIndices) {
                if (indexImage != null && !Strings.isNullOrEmpty(indexImage.url)) {
                    add(new ItemImageModel_()
                            .id(indexImage.url)
                            .url(indexImage.url)
                            .title(indexImage.title)
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

        footerModel_.status(status).addIf(status != null && status.loadMore, this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
