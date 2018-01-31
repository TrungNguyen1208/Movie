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

    void search(String query) {
        if (subscription != null) subscription.unsubscribe();
        subscription = searchObservable(query)
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
                .flatMap(new Func1<List<Media>, Observable<? extends List<Object>>>() {
                    @Override
                    public Observable<? extends List<Object>> call(List<Media> medias) {
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
