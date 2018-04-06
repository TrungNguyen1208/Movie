package ptit.nttrung.movie.ui.top_rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.data.remote.ApiUtils;

/**
 * Created by TrungNguyen on 4/6/2018.
 */

public class TopRateFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TopRateView {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private TopRatePresenter presenter;

    public static TopRateFragment newInstance() {
        Bundle args = new Bundle();

        TopRateFragment fragment = new TopRateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        presenter = new TopRatePresenter(ApiUtils.getApi());

        showProgress(true);
        return view;
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
//                    presenter.loadMovies(++currentPage);
                }
            });
        } else
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMovies(Response response) {

    }

    @Override
    public void showMoreMovies(List<Media> movies) {

    }

    @Override
    public void showError() {

    }

    @Override
    public void onRefresh() {

    }
}
