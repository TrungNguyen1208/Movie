package ptit.nttrung.movie.ui.list;


import java.util.List;

import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.ui.base.BaseView;


public interface MovieListView extends BaseView {

    void showProgress(boolean show);

    void showMovies(List<Media> movies);

    void showError();
}
