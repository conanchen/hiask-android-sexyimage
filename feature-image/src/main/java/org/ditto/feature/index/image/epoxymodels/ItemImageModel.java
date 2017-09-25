package org.ditto.feature.index.image.epoxymodels;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import org.ditto.feature.base.glide.GlideApp;
import org.ditto.feature.index.R;
import org.ditto.feature.index.R2;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This model shows an example of binding to a specific view type. In this case it is a custom view
 * we made, but it could also be another single view, like an EditText or Button.
 */
@EpoxyModelClass
public abstract class ItemImageModel extends EpoxyModelWithHolder<ItemImageModel.Holder> {
    @EpoxyAttribute
    String name;

    @EpoxyAttribute
    String path;

    @EpoxyAttribute
    int width;

    @EpoxyAttribute
    int selectIdx;

    @EpoxyAttribute
    int height;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener selectListener;

    @Override
    public void bind(Holder holder) {
//        holder.name.setText(name);
        holder.button_select.setOnClickListener(selectListener);
        holder.image.setOnClickListener(clickListener);
        holder.button_select.setChecked(selectIdx > 0);
        if (selectIdx > 0) {
            holder.button_select.setText("已置顶");
        }else{
            holder.button_select.setText("请置顶");
        }
        GlideApp
                .with(holder.image)
                .load("https://imgcache.cjmx.com/star/201512/20151201213056390.jpg")
                .placeholder(R.drawable.ask28)
                .error(R.drawable.ask28)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(withCrossFade())
                .into(holder.image);

    }

    @Override
    public void unbind(Holder holder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
        holder.button_select.setOnClickListener(null);

        holder.image.setOnClickListener(null);

        holder.image.setImageURI(null);

    }

    public static class Holder extends EpoxyHolder {
        @BindView(R2.id.image)
        AppCompatImageView image;

        @BindView(R2.id.button_select)
        AppCompatCheckBox button_select;


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
        return R.layout.image_item_model;
    }
}
