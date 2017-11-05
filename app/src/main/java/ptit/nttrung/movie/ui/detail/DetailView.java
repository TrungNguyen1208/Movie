package ptit.nttrung.movie.ui.detail;

import java.util.List;

import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.data.model.GenreManager;
import ptit.nttrung.movie.ui.base.BaseView;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public interface DetailView extends BaseView{
    void showCast(List<DetailResponse.Cast> list);

    void showGenre(List<GenreManager.Genre> list);

    void showEmptyGenre();

    void showEmpty();
}
