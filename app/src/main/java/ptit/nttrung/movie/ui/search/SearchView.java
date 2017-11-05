package ptit.nttrung.movie.ui.search;

import java.util.List;

import ptit.nttrung.movie.ui.base.BaseView;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public interface SearchView extends BaseView {
    void showProgress(boolean show);

    void showResult(List<Object> list);

    void showEmpty();

    void showError();
}
