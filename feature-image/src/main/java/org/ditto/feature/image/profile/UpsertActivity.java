package org.ditto.feature.image.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseActivity;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.VerticalGridCardSpacingDecoration;
import org.ditto.feature.base.glide.GlideApp;
import org.ditto.feature.image.R;
import org.ditto.feature.image.R2;
import org.ditto.feature.image.di.ImageViewModelFactory;
import org.ditto.lib.Constants;
import org.ditto.sexyimage.common.grpc.ImageType;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

@Route(path = "/feature_image/UpsertActivity")
public class UpsertActivity extends BaseActivity {
    private final static String TAG = UpsertActivity.class.getSimpleName();
    private final static Gson gson = new Gson();


    @Autowired(name = Constants.ROUTE_IMAGEURL)
    String mRouteImageUrl;

    private String mImageTitle = "";

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private CollapsingToolbarLayoutState mCollapsingToolbarLayoutState;

    @Inject
    ImageViewModelFactory mViewModelFactory;

    private ImageIndexViewModel mViewModel;
    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final ImageProfileEditController controller = new ImageProfileEditController(new ControllerCallback(), recycledViewPool);
    private static final int SPAN_COUNT = 1;

    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    @BindView(R2.id.app_bar)
    AppBarLayout app_bar;

    @BindView(R2.id.backdrop)
    AppCompatImageView image;

    @BindView(R2.id.fabupsert)
    FloatingActionButton fabupsertButon;

    @BindView(R2.id.fabdel)
    FloatingActionButton fabdelButon;

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
        app_bar.addOnOffsetChangedListener((AppBarLayout appBarLayout, int verticalOffset) -> {

            if (verticalOffset == 0) {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.EXPANDED) {
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    collapsingToolbarLayout.setTitle("EXPANDED");//设置title为EXPANDED

                    collapsingToolbarLayout.setTitle(mImageTitle);
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.COLLAPSED) {
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    buttonBarLayout.setVisibility(View.VISIBLE);//隐藏播放按钮
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                }
            } else {
                if (mCollapsingToolbarLayoutState != CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if (mCollapsingToolbarLayoutState == CollapsingToolbarLayoutState.COLLAPSED) {
                        buttonBarLayout.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                    }
                    collapsingToolbarLayout.setTitle("粉红图片");//设置title为INTERNEDIATE
                    mCollapsingToolbarLayoutState = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
        recyclerView.setItemAnimator(new SampleItemAnimator());
        recyclerView.setAdapter(controller.getAdapter());
    }


    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ImageIndexViewModel.class);
        ARouter.getInstance().inject(this);
        mViewModel.getLiveImageIndexForUpsert().observe(this, dataNisUpdate -> {
            controller.setData(dataNisUpdate.first, dataNisUpdate.second);
            mImageTitle = dataNisUpdate.first.title;
            if (dataNisUpdate.second.booleanValue()) {
                //isUpdate then show the delete button
                fabdelButon.setVisibility(View.VISIBLE);
            }
        });
        mViewModel.getLiveUpsertStatus().observe(this, status -> {
            Log.i(TAG, String.format("getLiveUpsertStatus status.code=%s \nstatus.message=%s", status.code, status.message));
            Toast.makeText(UpsertActivity.this, String.format("getLiveUpsertStatus status.code=%s \nstatus.message=%s", status.code, status.message), Toast.LENGTH_LONG).show();
        });

        mViewModel.getLiveDeleteStatus().observe(this, status -> {
            Log.i(TAG, String.format("getLiveDeleteStatus status.code=%s \nstatus.message=%s", status.code, status.message));
            Toast.makeText(UpsertActivity.this, String.format("getLiveDeleteStatus status.code=%s \nstatus.message=%s", status.code, status.message), Toast.LENGTH_LONG).show();
        });

        mViewModel.findForUpsert(mRouteImageUrl);
    }

    @OnClick(R2.id.fabupsert)
    public void onFabupsertButtonClicked() {
        mViewModel.saveUpdates();
    }

    @OnClick(R2.id.fabdel)
    public void onFabdelButtonClicked() {
        mViewModel.delete();
    }

    @OnClick(R2.id.saveButton)
    public void onSaveButtonClicked() {
        mViewModel.saveUpdates();
    }

    private class ControllerCallback implements ImageProfileEditController.AdapterCallbacks {
        @Override
        public void onUrlChanged(String imageUrl) {

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

            mViewModel.setNewUrl(imageUrl);
        }

        @Override
        public void onStatusChanged(boolean active, boolean toprank) {
            mViewModel.setNewActive(active).setNewToprank(toprank);
        }

        @Override
        public void onTitleChanged(String title) {
            mImageTitle = title;
            mViewModel.setNewTitle(title);
        }

        @Override
        public void onTypeChanged(ImageType type) {
            mViewModel.setNewType(type);
        }

        @Override
        public void onInfoUrlChanged(String url) {
            mViewModel.setNewInfoUrl(url);
        }
    }
}
