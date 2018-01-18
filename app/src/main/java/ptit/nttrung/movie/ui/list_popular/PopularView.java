package ptit.nttrung.movie.ui.list_popular;

import java.util.List;

import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.ui.base.BaseView;

/**
 * Created by TrungNguyen on 1/18/2018.
 */

public interface PopularView extends BaseView {
    void showProgress(boolean show);

    void showMovies(List<Media> movies);

    void showMoreMovies(List<Media> movies);

    void showError();
}
