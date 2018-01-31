package ptit.nttrung.movie.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import ptit.nttrung.movie.util.cache.ResponseCache;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MovieListView, MovieListAdapter.OnMovieClickListener{

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    private MovieListPresenter presenter;
    private MovieListAdapter adapter;
    private List<Media> movies = new ArrayList<>();
    private ResponseCache responseCache;

    public ListFragment() {
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

        presenter = new MovieListPresenter(responseCache, ApiUtils.getApi());
        presenter.attachView(this);
        adapter = new MovieListAdapter(movies);
        adapter.setOnMovieClickListener(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        showProgress(true);
        return view;
    }

    @Override
    public void onRefresh() {
        presenter.loadMovies(false);
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    presenter.loadMovies(true);
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
