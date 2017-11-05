package ptit.nttrung.movie.data.remote;

import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.data.model.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by TrungNguyen on 11/4/2017.
 */

public interface Api {
    @GET("movie/popular")
    Observable<Response> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Observable<Response> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Observable<DetailResponse> getCastList(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/images")
    Observable<DetailResponse> getImages(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Observable<DetailResponse> getVideos(@Query("api_key") String apiKey);

    @GET("search/multi")
    Observable<Response> search(@Query("api_key") String apiKey, @Query("query") String query);
}
