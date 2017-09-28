package org.ditto.feature.image.profile;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.Typed2EpoxyController;
import com.airbnb.epoxy.TypedEpoxyController;

import org.ditto.feature.image.index.epoxymodels.ItemImageModel_;
import org.ditto.feature.image.profile.epoxymodels.ImageInfoUrlEditModel_;
import org.ditto.feature.image.profile.epoxymodels.ImageStatusEditModel_;
import org.ditto.feature.image.profile.epoxymodels.ImageTitleEditModel;
import org.ditto.feature.image.profile.epoxymodels.ImageTitleEditModel_;
import org.ditto.feature.image.profile.epoxymodels.ImageTypeEditModel_;
import org.ditto.feature.image.profile.epoxymodels.ImageUrlEditModel;
import org.ditto.feature.image.profile.epoxymodels.ImageUrlEditModel_;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.sexyimage.grpc.Common;

import java.util.List;

public class ImageProfileEditController extends Typed2EpoxyController<IndexImage, Boolean> {
    public interface AdapterCallbacks {
        void onUrlChanged(String url);

        void onStatusChanged(boolean active, boolean toprank);

        void onTitleChanged(String title);

        void onTypeChanged(Common.ImageType type);

        void onInfoUrlChanged(String url);

    }


    private final AdapterCallbacks callbacks;
    private final RecycledViewPool recycledViewPool;


    @AutoModel
    ImageUrlEditModel_ imageUrlEditModel_;

    @AutoModel
    ImageInfoUrlEditModel_ imageInfoUrlEditModel_;

    @AutoModel
    ImageTitleEditModel_ imageTitleEditModel_;

    @AutoModel
    ImageTypeEditModel_ imageTypeEditModel_;

    @AutoModel
    ImageStatusEditModel_ imageStatusEditModel_;


    public ImageProfileEditController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
        this.callbacks = callbacks;
        this.recycledViewPool = recycledViewPool;
        setDebugLoggingEnabled(true);
    }

    @Override
    protected void buildModels(IndexImage indexImage, Boolean isUpdate) {
        imageUrlEditModel_.url(indexImage.url)
                .isUpdate(isUpdate)
                .callbacks(callbacks)
                .addTo(this);

        imageInfoUrlEditModel_.url(indexImage.infoUrl)
                .callbacks(callbacks)
                .addTo(this);

        imageTitleEditModel_.title(indexImage.title)
                .callbacks(callbacks)
                .addTo(this);

        imageTypeEditModel_.type(indexImage.type)
                .callbacks(callbacks)
                .addTo(this);

        imageStatusEditModel_.active(indexImage.active)
                .toprank(indexImage.toprank)
                .visitCount(indexImage.visitCount)
                .created(indexImage.created)
                .lastUpdated(indexImage.lastUpdated)
                .callbacks(callbacks).addTo(this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
        // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
        throw exception;
    }
}
