package ptit.nttrung.movie.ui.search;

import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.movie.BuildConfig;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.data.model.Section;
import ptit.nttrung.movie.data.remote.Api;
import ptit.nttrung.movie.data.remote.ApiUtils;
import ptit.nttrung.movie.ui.base.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class SearchPresenter extends Presenter<SearchView> {
    private Subscription subscription;
    private Api client;

    public SearchPresenter() {
        client = ApiUtils.getApi();
    }

    //Tìm kiếm các items bởi đoạn query
    // Ví dụ người dùng nhập chữ 'x' sau đó đến chữ 'y'.
    // Thì phần query sẽ là 'xy' và ta sẽ không cần quan tâm đến kết quả khi câu query là chữ 'x' nữa.
    // Vì thế hãy thoải mái mà chọn switchMap thôi!
    void search(String query) {
        if (subscription != null) subscription.unsubscribe();
        subscription = searchObservable(query)
                // khi một phần tử mới được emit, thì nó sẽ huỷ(unsubcribe)
                // Observable được tạo ra trước đó và sẽ chạy Observable mới.
                .switchMap(new Func1<Response, Observable<? extends Media>>() {
                    @Override
                    public Observable<? extends Media> call(Response response) {
                        return Observable.from(response.getResults());
                    }
                })
                .filter(new Func1<Media, Boolean>() {
                    @Override
                    public Boolean call(Media media) {
                        return media != null && !media.getMediaType().equals("tv");
                    }
                })
                .toList()
                //Operator flatMap sẽ không quan tâm đến thứ tự của các phần tử.
                // Nó sẽ tạo một Observable mới cho mỗi phần tử và không liên quan gì đến nhau.
                // Có phần tử sẽ emit nhanh, có phần tử emit chậm bởi vì trước đó mình đã tạo một đoạn delay ngẫu nhiên cho các phần tử.
                .flatMap(new Func1<List<Media>, Observable<? extends List<Object>>>() {
                    @Override
                    public Observable<? extends List<Object>> call(List<Media> medias) {
                        //Hàm zip() trong RxJava giúp bạn thực hiện đồng thời nhiều Observable
                        // và gộp các kết quả của các Observable lại cùng trong 1 kết quả trả về.
                        return Observable.zip(
                                SearchPresenter.this.extractSearchResult(medias, "person"),
                                SearchPresenter.this.extractSearchResult(medias, "movie"),
                                new Func2<List<Media>, List<Media>, List<Object>>() {
                                    @Override
                                    public List<Object> call(List<Media> people, List<Media> movies) {
                                        List<Object> list = new ArrayList<>();
                                        if (!movies.isEmpty()) {
                                            Section movieSection = new Section();
                                            movieSection.name = "Movies";
                                            list.add(movieSection);
                                            list.addAll(movies);
                                        }
                                        if (!people.isEmpty()) {
                                            Section peopleSection = new Section();
                                            peopleSection.name = "People";
                                            list.add(peopleSection);
                                            list.addAll(people);
                                        }
                                        return list;
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Object>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showProgress(false);
                        getView().showError();
                    }

                    @Override
                    public void onNext(List<Object> list) {
                        getView().showProgress(false);
                        if (!list.isEmpty())
                            getView().showResult(list);
                        else getView().showEmpty();
                    }

                    @Override
                    public void onStart() {
                        getView().showProgress(true);
                    }
                });
    }

    private Observable<List<Media>> extractSearchResult(List<Media> list, String type) {
        return Observable
                .from(list)
                .filter(new Func1<Media, Boolean>() {
                    @Override
                    public Boolean call(Media media) {
                        return media.getMediaType().equals(type);
                    }
                })
                .toList();
    }

    private Observable<Response> searchObservable(String query) {
        return client.search(BuildConfig.API_KEY, query);
    }
}
