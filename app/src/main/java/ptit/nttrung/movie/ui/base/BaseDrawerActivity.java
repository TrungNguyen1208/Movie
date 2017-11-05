package ptit.nttrung.movie.ui.base;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ptit.nttrung.movie.R;

/**
 * Created by TrungNguyen on 10/17/2017.
 */

public abstract class BaseDrawerActivity extends BaseActivity {

    DrawerLayout drawerLayout;
    NavigationView vNavigation;

    int avatarSize;
    String profilePhoto = "https://media1.britannica.com/eb-media/30/182830-004-61C7794A.jpg";

    private ImageView ivMenuUserProfilePhoto;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        bindViews();
        setupHeader();
        setNavigationItemSelected();
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        vNavigation = (NavigationView) findViewById(R.id.vNavigation);
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        Glide.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .centerCrop()
                .into(ivMenuUserProfilePhoto);
    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int[] startingLocation = new int[2];
//                v.getLocationOnScreen(startingLocation);
//                startingLocation[0] += v.getWidth() / 2;
//                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
//                overridePendingTransition(0, 0);
//            }
//        }, 200);
    }

    public abstract void setNavigationItemSelected();
}
