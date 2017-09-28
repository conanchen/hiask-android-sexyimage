package org.ditto.feature.image.profile.epoxymodels;

import android.view.View;
import android.widget.AutoCompleteTextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.image.R;
import org.ditto.feature.image.R2;
import org.ditto.feature.image.profile.ImageProfileEditController;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ImageUrlEditModel extends EpoxyModelWithHolder<ImageUrlEditModel.Holder> {
    @EpoxyAttribute
    String url;

    @EpoxyAttribute
    Boolean isUpdate;

    @EpoxyAttribute(DoNotHash)
    ImageProfileEditController.AdapterCallbacks callbacks;


    @Override
    public void bind(Holder holder) {
        holder.textViewUrl.setText(url);
        holder.textViewUrl.setOnFocusChangeListener((View view, boolean b) -> {
                    callbacks.onUrlChanged(holder.textViewUrl.getText().toString());
                }
        );
        holder.textViewUrl.setEnabled(!isUpdate);
    }

    @Override
    public void unbind(Holder holder) {

    }

    public static class Holder extends EpoxyHolder {
        @BindView(R2.id.textViewUrl)
        AutoCompleteTextView textViewUrl;


        View view;

        @Override
        protected void bindView(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 1;
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.image_urledit_model;
    }
}
