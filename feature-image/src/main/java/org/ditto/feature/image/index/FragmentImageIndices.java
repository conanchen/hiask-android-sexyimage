package org.ditto.feature.image.index;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.image.R;
import org.ditto.feature.image.R2;
import org.ditto.feature.image.di.ImageViewModelFactory;
import org.ditto.lib.Constants;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.repository.util.Status;
import org.ditto.sexyimage.grpc.Common;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a listCommandsBy of Items.
 * <p/>
 */
public class FragmentImageIndices extends BaseFragment implements Injectable, ImageIndicesController.AdapterCallbacks {
    private final static String TAG = FragmentImageIndices.class.getSimpleName();
    private final static Gson gson = new Gson();

    @Inject
    ImageViewModelFactory viewModelFactory;

    private ImageIndicesViewModel viewModel;

    private static final int SPAN_COUNT = 3;

    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final ImageIndicesController controller = new ImageIndicesController(this, recycledViewPool);

    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentImageIndices() {
    }


    public static FragmentImageIndices create(String title, Common.ImageType type) {
        Preconditions.checkNotNull(title);
        FragmentImageIndices fragment = new FragmentImageIndices();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, title);
        bundle.putString(Constants.IMAGETYPE, type.name());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupController();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
    }


    private void setupController() {
        Timber.d("calling setupController");
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageIndicesViewModel.class);

        viewModel.getLiveImageIndices().observe(this, dataNstatus -> {
            controller.setData(dataNstatus);
        });

        viewModel.refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new SampleItemAnimator());

        recyclerView.setAdapter(controller.getAdapter());


        return recyclerView;
    }


    @Override
    public void onMessageItemClicked(IndexImage indexImageIssue, int position) {
        ARouter.getInstance().build("/feature_image/UpsertActivity")
                .withString(Constants.ROUTE_IMAGEURL, indexImageIssue.url)
                .navigation();
    }
}
