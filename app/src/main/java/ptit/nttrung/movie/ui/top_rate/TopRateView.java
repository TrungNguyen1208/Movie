package ptit.nttrung.movie.ui.top_rate;

import java.util.List;

import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.ui.base.BaseView;

/**
 * Created by TrungNguyen on 4/6/2018.
 */

public interface TopRateView extends BaseView {
    void showProgress(boolean show);

    void showMovies(Response response);

    void showMoreMovies(List<Media> movies);

    void showErrorView(Throwable e);

    void showErrorLoadMore(Throwable e);

    void hideErrorView();
}

