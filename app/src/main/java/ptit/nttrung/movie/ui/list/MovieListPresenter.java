package ptit.nttrung.movie.ui.list;

import java.util.List;

import ptit.nttrung.movie.BuildConfig;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.data.remote.Api;
import ptit.nttrung.movie.ui.base.Presenter;
import ptit.nttrung.movie.util.cache.ResponseCache;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MovieListPresenter extends Presenter<MovieListView> {
    private final CompositeSubscription subscription;
    private ResponseCache cache;
    private Api client;

    private static final String API_KEY = "27c669d719af09d66f0b116ee766a3b7";

    public MovieListPresenter(ResponseCache responseCache, Api api) {
        subscription = new CompositeSubscription();
        cache = responseCache;
        client = api;
    }

    @Override
    public void attachView(MovieListView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscription.unsubscribe();
    }

    void loadMovies(boolean allowCache) {
        Subscriber<List<Media>> subscriber = new Subscriber<List<Media>>() {
            @Override
            public void onCompleted() {
                getView().showProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                getView().showProgress(false);
                getView().showError();
            }

            @Override
            public void onNext(List<Media> movies) {
                getView().showMovies(movies);
            }
        };

        subscription.add((allowCache ? Observable.concat(getCacheObservable(), getMovieObservable()) : getMovieObservable())
                .filter(list -> list != null && list.size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    private Observable<List<Media>> getMovieObservable() {
        return Observable.defer(new Func0<Observable<List<Media>>>() {
            @Override
            public Observable<List<Media>> call() {
                return client
                        .getNowPlayingMovies(BuildConfig.API_KEY)
                        .flatMap(new Func1<Response, Observable<? extends List<Media>>>() {
                            @Override
                            public Observable<? extends List<Media>> call(Response response) {
                                return Observable.just(response.getResults());
                            }
                        })
                        .doOnNext(new Action1<List<Media>>() {
                            @Override
                            public void call(List<Media> list) {
                                cache.insert("movies", list);
                            }
                        });
            }
        });
    }

    private Observable<List<Media>> getCacheObservable() {
        return Observable.defer(new Func0<Observable<List<Media>>>() {
            @Override
            public Observable<List<Media>> call() {
                List<Media> list = cache.get("movies");
                return Observable.just(list);
            }
        });
    }
}
