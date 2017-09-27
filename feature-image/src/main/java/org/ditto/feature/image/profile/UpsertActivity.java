package org.ditto.feature.image.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.VerticalGridCardSpacingDecoration;
import org.ditto.feature.base.glide.GlideApp;
import org.ditto.feature.image.R;
import org.ditto.feature.image.R2;
import org.ditto.feature.image.di.ImageViewModelFactory;
import org.ditto.feature.image.index.ImageIndicesViewModel;
import org.ditto.lib.Constants;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.sexyimage.grpc.Common;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

@Route(path = "/feature_image/UpsertActivity")
public class UpsertActivity extends BaseActivity implements ImageProfileEditController.AdapterCallbacks {

    @Autowired(name= Constants.IMAGEURL)
    String imageUrl;

    private IndexImage mOldIndexImage = IndexImage
            .builder()
            .setUrl("http://n.7k7kimg.cn/2013/0316/1363403583271.jpg")
            .setInfoUrl("http://n.7k7kimg.cn/2013/0316/1363403583271.jpg")
            .setType(Common.ImageType.NORMAL.name())
            .setLastUpdated(System.currentTimeMillis())
            .setActive(true)
            .setToprank(false)
            .build();
    private IndexImage newIndexImage = new IndexImage();

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CollapsingToolbarLayoutState state;

    @Inject
    ImageViewModelFactory viewModelFactory;

    private ImageIndicesViewModel viewModel;
    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final ImageProfileEditController controller = new ImageProfileEditController(this, recycledViewPool);
    GridLayoutManager gridLayoutManager;
    private static final int SPAN_COUNT = 1;


    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    @BindView(R2.id.app_bar)
    AppBarLayout app_bar;

    @BindView(R2.id.backdrop)
    AppCompatImageView image;

    @BindView(R2.id.fab)
    FloatingActionButton fabButon;

    @BindView(R2.id.toolbar_button_layout)
    ButtonBarLayout buttonBarLayout;

    @BindView(R2.id.saveButton)
    ImageButton saveButton;

    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upsert);
        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupRecyclerView();

        setupViewModel();
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        collapsingToolbarLayout.setTitle("EXPANDED");//设置title为EXPANDED
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        collapsingToolbarLayout.setTitle("");//设置title不显示
                        buttonBarLayout.setVisibility(View.VISIBLE);//隐藏播放按钮
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            buttonBarLayout.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                        }
                        collapsingToolbarLayout.setTitle("粉红猪小妹");//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });

    }

    private void setupRecyclerView() {
        // Many carousels and color models are shown on screen at once. The default recycled view
        // pool size is only 5, so we manually set the pool size to avoid constantly creating new views
        // We also use a shared view pool so that carousels can recycle items between themselves.
//        recycledViewPool.setMaxRecycledViews(R.layout.model_color, Integer.MAX_VALUE);
//        recycledViewPool.setMaxRecycledViews(R.layout.model_carousel_group, Integer.MAX_VALUE);
        recyclerView.setRecycledViewPool(recycledViewPool);

        // We are using a multi span grid to allow two columns of buttons. To set this up we need
        // to set our span count on the controller and then get the span size lookup object from
        // the controller. This look up object will delegate span size lookups to each model.
        controller.setSpanCount(SPAN_COUNT);
        gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
        recyclerView.setItemAnimator(new SampleItemAnimator());
        recyclerView.setAdapter(controller.getAdapter());
    }


    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageIndicesViewModel.class);
        controller.setData(mOldIndexImage);
    }

    @OnClick(R2.id.fab)
    public void onFabButtonClicked() {
        Toast.makeText(this, "onFabButtonClicked", Toast.LENGTH_LONG).show();
    }

    @OnClick(R2.id.saveButton)
    public void onSaveButtonClicked() {
        Toast.makeText(this, "onSaveButtonClicked", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onUrlChanged(String imageUrl) {
        Toast.makeText(this, "onUrlChanged " + imageUrl, Toast.LENGTH_LONG).show();
        GlideApp
                .with(image)
//                .load("http://n.7k7kimg.cn/2013/0316/1363403583271.jpg")
                .load(imageUrl)
                .placeholder(R.drawable.ask28)
                .error(R.drawable.ask28)
                .fallback(new ColorDrawable(Color.GRAY))
                .centerCrop()
                .transition(withCrossFade())
                .into(image);

        newIndexImage.url = imageUrl;
    }

    @Override
    public void onStatusChanged(boolean active, boolean toprank) {
        newIndexImage.active = active;
        newIndexImage.toprank = toprank;
    }

    @Override
    public void onTitleChanged(String title) {
        newIndexImage.title = title;
    }

    @Override
    public void onTypeChanged(Common.ImageType type) {
        newIndexImage.type = type.name();
    }

    @Override
    public void onInfoUrlChanged(String url) {
        newIndexImage.infoUrl = url;
    }
}
