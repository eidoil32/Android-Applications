package com.idohayun.mybusiness;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryView extends Fragment {
    private static List<ImageURL> imageURLList;
    private static GridView gridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GetImagesURLsFromServer getImagesURLsFromServer = new GetImagesURLsFromServer();
    private static final String TAG = "GalleryView";
    private static View viewForSwipe;

    public GalleryView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.changeTitlePage(inflater.getContext().getResources().getString(R.string.text_gallery_title));
        return inflater.inflate(R.layout.fragment_gallery_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(view.getResources().getColor(R.color.colorBackground,null));
        gridView = view.findViewById(R.id.gallery_grid_view);
        getImagesURLsFromServer = new GetImagesURLsFromServer();
        getImagesURLsFromServer.CreateImageGridView(view.getContext(),gridView);
        viewForSwipe = view;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.gallery_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: here!");
                CustomToast.showToast(view.getContext(),view.getResources().getString(R.string.refresh_gallery_msg),2);
                gridView.setAdapter(null);
                GetImagesURLsFromServer getImagesURLsFromServer = new GetImagesURLsFromServer();
                getImagesURLsFromServer.CreateImageGridView(viewForSwipe.getContext(),gridView);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
