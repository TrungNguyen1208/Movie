package ptit.nttrung.movie.ui.castlist;

import android.annotation.TargetApi;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.data.model.DetailResponse;
import ptit.nttrung.movie.ui.detail.CastAdapter;

/**
 * Created by TrungNguyen on 11/5/2017.
 */

public class CastListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    private List<DetailResponse.Cast> list;
    private LinearLayoutManager layoutManager;

    //stole from the plaid app
    private RecyclerView.OnScrollListener toolbarElevation = new RecyclerView.OnScrollListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && layoutManager.findFirstVisibleItemPosition() == 0
                    && layoutManager.findViewByPosition(0).getTop() == recyclerView.getPaddingTop()
                    && toolbar.getTranslationZ() != 0) {
                // at top, reset elevation
                toolbar.setTranslationZ(0f);
            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                    && toolbar.getTranslationZ() != -1f) {
                // grid scrolled, lower toolbar to allow content to pass in front
                toolbar.setTranslationZ(-1f);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Billed Cast");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24px);

        initPadding();
        list = getIntent().getParcelableArrayListExtra("cast");
        recyclerView.addOnScrollListener(toolbarElevation);
        CastAdapter adapter = new CastAdapter(list, CastAdapter.TYPE_FULL);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
            });
        }
    }

    private void initPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                    new int[]{android.R.attr.actionBarSize});
            int actionBarSize = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
            recyclerView.setPadding(0, actionBarSize, 0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
