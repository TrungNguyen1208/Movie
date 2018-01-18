package ptit.nttrung.movie.ui.list_popular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.remote.ApiUtils;
import ptit.nttrung.movie.ui.detail.DetailActivity;
import ptit.nttrung.movie.util.EndlessRecyclerViewScrollListener;
import ptit.nttrung.movie.util.cache.ResponseCache;

/**
 * Created by TrungNguyen on 1/18/2018.
 */

public class ListPopularFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PopularView, MoviePopularAdapter.OnMovieClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private MoviePopularAdapter adapter;
    private PopularPresenter presenter;
    private List<Media> movies = new ArrayList<>();
    private ResponseCache responseCache;

    public ListPopularFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        try {
            responseCache = new ResponseCache(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        presenter = new PopularPresenter(responseCache, ApiUtils.getApi());
        presenter.attachView(this);
        adapter = new MoviePopularAdapter(movies);
        adapter.setOnMovieClickListener(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page = page + 1;
                Log.e("TAG", "page: " + page);
                presenter.loadMoreMovies(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        swipeRefreshLayout.setOnRefreshListener(this);

        showProgress(true);

        return view;
    }


    @Override
    public void onRefresh() {
        presenter.loadMovies(1);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    presenter.loadMovies(1);
                }
            });
        } else
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMovies(List<Media> list) {
        movies.clear();
        movies.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreMovies(List<Media> list) {
        int curSize = adapter.getItemCount();
        movies.addAll(list);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemRangeInserted(curSize, movies.size() - 1);
            }
        });
    }

    @Override
    public void showError() {

    }

    @Override
    public void onMovieClicked(Media media, View view) {
        if (media.getPosterPath() != null)
            DetailActivity.navigate(getActivity(), view, media);
        else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("media", media);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }
}
