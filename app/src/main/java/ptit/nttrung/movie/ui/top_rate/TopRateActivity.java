package ptit.nttrung.movie.ui.top_rate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;

import ptit.nttrung.movie.R;
import ptit.nttrung.movie.ui.base.BaseDrawerActivity;
import ptit.nttrung.movie.ui.list.MainActivity;
import ptit.nttrung.movie.ui.list_popular.ListPopularFragment;

/**
 * Created by TrungNguyen on 4/6/2018.
 */

public class TopRateActivity extends BaseDrawerActivity {

    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        TopRateFragment fragment = TopRateFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        getSupportActionBar().setTitle("Top Rate");
    }


    @Override
    public void setNavigationItemSelected() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView vNavigation = (NavigationView) findViewById(R.id.vNavigation);

        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_news:
                        startActivity(new Intent(TopRateActivity.this, MainActivity.class));
                        TopRateActivity.this.finish();
                    case R.id.menu_popular:
                        startActivity(new Intent(TopRateActivity.this, ListPopularFragment.class));
                        TopRateActivity.this.finish();
                        break;
                    case R.id.menu_like:
                        drawerLayout.closeDrawer(Gravity.LEFT, true);
                        break;
                    default:
                        break;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                return true;
            }
        });
    }
}
