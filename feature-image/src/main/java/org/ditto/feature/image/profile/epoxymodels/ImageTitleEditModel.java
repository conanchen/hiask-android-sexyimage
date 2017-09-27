package org.ditto.feature.image.profile.epoxymodels;

import android.support.v7.widget.AppCompatEditText;
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

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ImageTitleEditModel extends EpoxyModelWithHolder<ImageTitleEditModel.Holder> {
    @EpoxyAttribute
    String title;


    @EpoxyAttribute(DoNotHash)
    ImageProfileEditController.AdapterCallbacks callbacks;


    @Override
    public void bind(Holder holder) {

        holder.textViewTitle.setText(title);
        holder.textViewTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                callbacks.onTitleChanged(holder.textViewTitle.getText().toString());
            }
        });
    }

    @Override
    public void unbind(Holder holder) {

    }

    public static class Holder extends EpoxyHolder {

        @BindView(R2.id.textViewTitle)
        AppCompatEditText textViewTitle;


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
        return R.layout.image_titleedit_model;
    }
}
