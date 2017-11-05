package ptit.nttrung.movie.ui.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.Media;
import ptit.nttrung.movie.ui.detail.DetailActivity;
import ptit.nttrung.movie.ui.widget.ResettableEditText;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class SearchActivity extends AppCompatActivity implements SearchView, ResettableEditText.ClearListener, SearchAdapter.OnMediaClickListener {
    ResettableEditText editText;
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView indicator;

    private SearchPresenter presenter = new SearchPresenter();
    private Subscription subscription;
    private SearchAdapter adapter;
    private List<Object> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        presenter.attachView(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        editText = (ResettableEditText) findViewById(R.id.search_edit_text);
        indicator = (TextView) findViewById(R.id.indication);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupListener();
        setupRecyclerView();
    }

    private void setupListener() {
        subscription = RxTextView.textChanges(editText)
                .debounce(1, TimeUnit.SECONDS)
                .switchMap(new Func1<CharSequence, Observable<? extends CharSequence>>() {
                    @Override
                    public Observable<? extends CharSequence> call(CharSequence value) {
                        return Observable.just(value);
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence s) {
                        return s != null && s.length() >= 2;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        presenter.search(charSequence.toString());
                    }
                });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(list);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        subscription.unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) indicator.setVisibility(View.GONE);
    }

    @Override
    public void showResult(List<Object> list) {
        this.list.clear();
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmpty() {
        indicator.setVisibility(View.VISIBLE);
        indicator.setText(getString(R.string.empty));
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        indicator.setVisibility(View.VISIBLE);
        indicator.setText(getString(R.string.error));
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onTextCleared() {
        this.list.clear();
        adapter.notifyDataSetChanged();
        showProgress(false);
    }

    @Override
    public void onMovieClicked(View view, Media movie) {
        DetailActivity.navigate(this, view, movie);
    }

    @Override
    public void onPersonClicked(View view, Media person) {
        Toast.makeText(getApplicationContext(), person.getName(), Toast.LENGTH_SHORT).show();
    }
}
