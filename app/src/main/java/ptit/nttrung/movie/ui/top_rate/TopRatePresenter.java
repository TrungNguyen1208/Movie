package ptit.nttrung.movie.ui.top_rate;

import java.util.List;

import ptit.nttrung.movie.BuildConfig;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.model.Response;
import ptit.nttrung.movie.data.remote.Api;
import ptit.nttrung.movie.ui.base.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by TrungNguyen on 4/6/2018.
 */

public class TopRatePresenter extends Presenter<TopRateView> {
    private final CompositeSubscription subscription;
    private Api client;

    public TopRatePresenter(Api api) {
        subscription = new CompositeSubscription();
        client = api;
    }

    @Override
    public void attachView(TopRateView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
        subscription.unsubscribe();
    }

    void loadMovies(int page) {
        getView().hideErrorView();
        Subscriber<Response> sub = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                getView().showProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                getView().showProgress(false);
                getView().showErrorView(e);
            }

            @Override
            public void onNext(Response response) {
                getView().hideErrorView();
                getView().showMovies(response);
            }
        };

        subscription.add(getResponseObservable(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sub));
    }

    void loadMoreMovies(int page) {
        Subscriber<List<Media>> subscriber = new Subscriber<List<Media>>() {
            @Override
            public void onCompleted() {
                getView().showProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                getView().showProgress(false);
                getView().showErrorLoadMore(e);
            }

            @Override
            public void onNext(List<Media> movies) {
                getView().showMoreMovies(movies);
            }
        };

        subscription.add(getMovieObservable(page)
                .filter(new Func1<List<Media>, Boolean>() {
                    @Override
                    public Boolean call(List<Media> list) {
                        return list != null && list.size() > 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    private Observable<List<Media>> getMovieObservable(int page) {
        return Observable.defer(new Func0<Observable<List<Media>>>() {
            @Override
            public Observable<List<Media>> call() {
                return getResponseObservable(page)
                        .flatMap(new Func1<Response, Observable<? extends List<Media>>>() {
                            @Override
                            public Observable<? extends List<Media>> call(Response response) {
                                return Observable.just(response.getResults());
                            }
                        });
            }
        });
    }

    private Observable<Response> getResponseObservable(int page) {
        return client.getTopRatedMovies(BuildConfig.API_KEY, "en_US", page);
    }
}
