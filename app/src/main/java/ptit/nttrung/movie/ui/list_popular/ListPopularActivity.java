package ptit.nttrung.movie.ui.list_popular;

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
import ptit.nttrung.movie.ui.top_rate.TopRateActivity;

/**
 * Created by TrungNguyen on 1/18/2018.
 */

public class ListPopularActivity extends BaseDrawerActivity {

    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        ListPopularFragment fragment = new ListPopularFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        getSupportActionBar().setTitle("Movie Popular");
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
                        startActivity(new Intent(ListPopularActivity.this, MainActivity.class));
                        ListPopularActivity.this.finish();
                    case R.id.menu_popular:
                        drawerLayout.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.menu_like:
                        startActivity(new Intent(ListPopularActivity.this, TopRateActivity.class));
                        ListPopularActivity.this.finish();
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
