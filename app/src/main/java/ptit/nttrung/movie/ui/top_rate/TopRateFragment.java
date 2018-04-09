package ptit.nttrung.movie.ui.top_rate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeoutException;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.data.remote.ApiUtils;
import ptit.nttrung.movie.util.PaginationAdapterCallback;
import ptit.nttrung.movie.util.PaginationScrollListener;

/**
 * Created by TrungNguyen on 4/6/2018.
 */

public class TopRateFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TopRateView {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout errorLayout;
    private TextView txtError;

    private TopRatePresenter presenter;
    private TopRateAdapter adapter;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int TOTAL_PAGES = 5;

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
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        txtError = (TextView) view.findViewById(R.id.error_txt_cause);

        presenter = new TopRatePresenter(ApiUtils.getApi());
        presenter.attachView(this);

        this.adapter = new TopRateAdapter(getContext());
        adapter.setmCallback(new PaginationAdapterCallback() {
            @Override
            public void retryPageLoad() {
                presenter.loadMoreMovies(currentPage);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                presenter.loadMoreMovies(currentPage);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

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
                    presenter.loadMovies(currentPage);
                }
            });
        } else
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMovies(Response response) {
        this.TOTAL_PAGES = response.getTotalPages();
        adapter.addAll(response.getResults());

        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void showMoreMovies(List<Media> movies) {
        adapter.removeLoadingFooter();
        isLoading = false;

        adapter.addAll(movies);

        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void showErrorView(Throwable throwable) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    public void showErrorLoadMore(Throwable throwable) {
        adapter.showRetry(true, fetchErrorMessage(throwable));
    }

    public void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        this.currentPage = PAGE_START;
        showProgress(true);
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getContext().getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getContext().getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
