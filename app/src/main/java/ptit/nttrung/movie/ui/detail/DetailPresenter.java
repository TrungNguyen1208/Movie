package ptit.nttrung.movie.ui.detail;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import ptit.nttrung.movie.BuildConfig;
import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.data.model.GenreManager;
import ptit.nttrung.movie.data.remote.Api;
import ptit.nttrung.movie.ui.base.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class DetailPresenter extends Presenter<DetailView> {
    private CompositeSubscription subscription;
    private AssetManager assetManager;
    private Api client;
    private int movieId;

    public DetailPresenter(AssetManager assetManager, Api client) {
        subscription = new CompositeSubscription();
        this.assetManager = assetManager;
        this.client = client;
    }

    void setMovieId(int id) {
        this.movieId = id;
    }

    @Override
    public void detachView() {
        super.detachView();
        subscription.unsubscribe();
    }

    void loadCast() {
        subscription.add(
                getCastListJsonObservable()
                        .flatMap(new Func1<DetailResponse, Observable<? extends DetailResponse.Cast>>() {
                            @Override
                            public Observable<? extends DetailResponse.Cast> call(DetailResponse castResponse) {
                                return Observable.from(castResponse.getCast());
                            }
                        })
                        .filter(new Func1<DetailResponse.Cast, Boolean>() {
                            @Override
                            public Boolean call(DetailResponse.Cast cast) {
                                return cast.getProfilePath() != null;
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<DetailResponse.Cast>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().showEmpty();
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(List<DetailResponse.Cast> casts) {
                                if (!casts.isEmpty()) {
                                    getView().showCast(casts);
                                } else {
                                    getView().showEmpty();
                                }
                            }
                        })
        );
    }

    void loadImages() {
        subscription.add(
                Observable.zip(getImages(), getVideos(),
                        new Func2<List<DetailResponse.Image>, List<DetailResponse.Video>, DetailResponse>() {
                            @Override
                            public DetailResponse call(List<DetailResponse.Image> images, List<DetailResponse.Video> videos) {
                                DetailResponse response = new DetailResponse();
                                response.setPosters(images);
                                response.setVideos(videos);
                                return response;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<DetailResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(DetailResponse detailResponse) {

                            }
                        })
        );
    }

    void loadGenres(List<Integer> genreIds) {
        if (genreIds.isEmpty()) return;
        subscription.add(getGenreObservable()
                .map(new Func1<GenreManager, List<GenreManager.Genre>>() {
                    @Override
                    public List<GenreManager.Genre> call(GenreManager genreManager) {
                        return genreManager.getGenreList(genreIds);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<GenreManager.Genre>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getView().showEmptyGenre();
                    }

                    @Override
                    public void onNext(List<GenreManager.Genre> genres) {
                        getView().showGenre(genres);
                    }
                })
        );
    }

    private Observable<GenreManager> getGenreObservable() {
        try {
            InputStream is = assetManager.open("genres.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Type type = new TypeToken<GenreManager>() {
            }.getType();
            return Observable.just(new Gson().fromJson(json, type));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    private Observable<DetailResponse> getCastListJsonObservable() {
        return client.getCastList(movieId, BuildConfig.API_KEY);
    }

    private Observable<List<DetailResponse.Image>> getImages() {
        return client.getImages(BuildConfig.API_KEY)
                .flatMap(detailResponse -> Observable.just(detailResponse.getBackdrops(), detailResponse.getPosters()));
    }

    private Observable<List<DetailResponse.Video>> getVideos() {
        return client.getVideos(BuildConfig.API_KEY)
                .flatMap(detailResponse -> Observable.just(detailResponse.getVideos()));
    }
}
