package ptit.nttrung.movie.data.remote;

/**
 * Created by TrungNguyen on 11/4/2017.
 */

public class ApiUtils {
    public static final String BASE_URL = "http://api.themoviedb.org/3/";

    public static Api getApi(){
        return RetrofitClient.getClient(BASE_URL).create(Api.class);
    }
}
