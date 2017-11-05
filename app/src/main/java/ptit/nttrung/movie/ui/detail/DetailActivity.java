package ptit.nttrung.movie.ui.detail;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.data.model.GenreManager;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.data.remote.ApiUtils;
import ptit.nttrung.movie.util.DateUtil;
import ptit.nttrung.movie.util.GlideUtil;
import ptit.nttrung.movie.util.UrlBuilder;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class DetailActivity extends AppCompatActivity implements DetailView {

    View container;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ImageView backdrop, poster;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView title, releaseDate, overview, rating;
    RecyclerView castRecyclerView,genreRecyclerView;

    private CastAdapter castAdapter;
    private GenreAdapter genreAdapter;
    private List<DetailResponse.Cast> casts = new ArrayList<>();
    private List<GenreManager.Genre> genres = new ArrayList<>();

    private DetailPresenter presenter;

    public static void navigate(Activity context, View view, Media media) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("media", media);
        ImageView coverStartView = (ImageView) view.findViewById(R.id.poster);
        if (coverStartView.getDrawable() == null) {
            context.startActivity(intent);
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, coverStartView, "poster");
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTransition();
        setContentView(R.layout.activity_detail);

        presenter = new DetailPresenter(getAssets(), ApiUtils.getApi());
        presenter.attachView(this);

        bindView();
        loadInfoMedia();
    }

    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backdrop = (ImageView) findViewById(R.id.backdrop);
        poster = (ImageView) findViewById(R.id.poster);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        container = (View) findViewById(R.id.layout_info);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        title = (TextView) findViewById(R.id.title);
        releaseDate = (TextView) findViewById(R.id.release_date);
        overview = (TextView) findViewById(R.id.overview);
        rating = (TextView) findViewById(R.id.rating);
        castRecyclerView = (RecyclerView) findViewById(R.id.cast_recycler_view);
        genreRecyclerView = (RecyclerView) findViewById(R.id.genre_recycler_view);
    }

    private void loadInfoMedia() {
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        Media media = getIntent().getParcelableExtra("media");
        if (media.getBackgroundColor() != 0) {
            container.setBackgroundColor(media.getBackgroundColor());
            appBarLayout.setBackgroundColor(media.getBackgroundColor());
        }
        GlideUtil.load(this, UrlBuilder.getPosterUrl(media.getPosterPath()), poster);
        GlideUtil.load(this, UrlBuilder.getBackdropUrl(media.getBackdropPath()), backdrop);
        doCircularReveal();

        title.setText(media.getTitle());
        releaseDate.setText(DateUtil.format(media.getReleaseDate()));
        overview.setText(media.getOverview());
        rating.setText(media.getVoteAverage() + " from " + media.getVoteCount() + " votes");

        initAdapter();
        presenter.setMovieId(media.getId());
        presenter.loadCast();
        presenter.loadGenres(media.getGenreIds());
    }

    private void initAdapter() {
        castAdapter = new CastAdapter(casts, CastAdapter.TYPE_SUMMARY);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        castRecyclerView.setAdapter(castAdapter);

        genreAdapter = new GenreAdapter(genres);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        genreRecyclerView.setAdapter(genreAdapter);
    }


    private void doCircularReveal() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            backdrop.post(() -> {
                int centerX = backdrop.getMeasuredWidth() / 2;
                int centerY = backdrop.getMeasuredHeight() / 2;
                int endRadius = (int) Math
                        .hypot(backdrop.getWidth(), backdrop.getHeight());
                Animator animator = ViewAnimationUtils.createCircularReveal(backdrop, centerX, centerY, 0, endRadius);
                animator.setDuration(800);
                backdrop.setVisibility(View.VISIBLE);
                animator.start();
            });
        }
    }

    private void setActivityTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade transition = new Fade();
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    @Override
    public void showCast(List<DetailResponse.Cast> list) {
        findViewById(R.id.cast_container).setVisibility(View.VISIBLE);
        casts.clear();
        casts.addAll(list);
        castAdapter.notifyDataSetChanged();
    }

    @Override
    public void showGenre(List<GenreManager.Genre> list) {
        findViewById(R.id.genre_container).setVisibility(View.VISIBLE);
        genres.clear();
        genres.addAll(list);
        genreAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyGenre() {
        findViewById(R.id.genre_container).setVisibility(View.GONE);
    }

    @Override
    public void showEmpty() {
        findViewById(R.id.cast_container).setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
