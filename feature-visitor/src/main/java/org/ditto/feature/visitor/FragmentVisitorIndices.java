package org.ditto.feature.visitor;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.common.base.Preconditions;

import org.ditto.feature.base.BaseFragment;
import org.ditto.feature.base.SampleItemAnimator;
import org.ditto.feature.base.VerticalGridCardSpacingDecoration;
import org.ditto.feature.base.di.Injectable;
import org.ditto.feature.visitor.di.VisitorViewModelFactory;
import org.ditto.lib.Constants;
import org.ditto.lib.dbroom.index.IndexImage;
import org.ditto.lib.dbroom.index.IndexVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment representing a listCommandsBy of Items.
 * <p/>
 */
public class FragmentVisitorIndices extends BaseFragment implements Injectable, VisitorIndicesController.AdapterCallbacks {

    @Inject
    VisitorViewModelFactory viewModelFactory;

    private VisitorIndicesViewModel viewModel;

    private static final int SPAN_COUNT = 1;
    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 3 * SPAN_COUNT;
    private boolean loading = false;

    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final VisitorIndicesController controller = new VisitorIndicesController(this, recycledViewPool);

    @BindView(R2.id.itemlist)
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentVisitorIndices() {
    }

    public static FragmentVisitorIndices create(String title) {
        Preconditions.checkNotNull(title);
        FragmentVisitorIndices fragment = new FragmentVisitorIndices();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, title);
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(VisitorIndicesViewModel.class);
        Map<String, Object> datas = new HashMap<>();
        datas.put(Constants.DATA1, new ArrayList<IndexImage>());

        viewModel.getLiveVisitorIndices().observe(this, messages -> {
            datas.put(Constants.DATA1, messages);
            controller.setData((List<IndexVisitor>) datas.get(Constants.DATA1));
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);

        // Many carousels and color models are shown on screen at once. The default recycled view
        // pool size is only 5, so we manually set the pool size to avoid constantly creating new views
        // We also use a shared view pool so that carousels can recycle items between themselves.
        // recycledViewPool.setMaxRecycledViews(R.layout.model_color, Integer.MAX_VALUE);
        // recycledViewPool.setMaxRecycledViews(R.layout.model_carousel_group, Integer.MAX_VALUE);
        recyclerView.setRecycledViewPool(recycledViewPool);

        // We are using a multi span grid to allow two columns of buttons. To set this up we need
        // to set our span count on the controller and then get the span size lookup object from
        // the controller. This look up object will delegate span size lookups to each model.
        controller.setSpanCount(SPAN_COUNT);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
        recyclerView.setItemAnimator(new SampleItemAnimator());
        recyclerView.setAdapter(controller.getAdapter());


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        Timber.d("callback.onScrollToBottom() +1,viewModel.loadMore()");
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        Timber.d("callback.onScrollToTop() -1,viewModel.refresh()");
                    }
                }

            }
        });

        return recyclerView;
    }

    @Override
    public void onItemUserClicked(IndexVisitor carousel, int colorPosition) {
        ARouter.getInstance().build("/feature_chat/PersonChatActivity").navigation();
    }

    @Override
    public void onItemUgroupClicked(IndexVisitor carousel, int colorPosition) {
        Snackbar.make(recyclerView, "TODO: onItemUgroupClicked------", Snackbar.LENGTH_LONG).show();
    }
}
