package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appdevelopment.adapters.ViewPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    Intent intent;
    Bundle bundle;
    TextView tvUsername;
    // Thêm biến toàn cục để lưu thông tin user
    private int currentUserId;
    private String currentUsername;
    private String currentEmail;
    private int currentRole;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager2 = findViewById(R.id.viewPager);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
         //tvUsername = findViewById(R.id.tvName);
        intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null){
            currentUserId = bundle.getInt("ID_ACCOUNT", -1);
            currentUsername = bundle.getString("USERNAME_ACCOUNT", "");
            currentEmail = bundle.getString("EMAIL_ACCOUNT", "");
            currentRole = bundle.getInt("ROLE_ACCOUNT", 0);
            //tvUsername.setText(currentUsername);
        } else {
            // Không có dữ liệu user, quay về LoginActivity
            Intent loginIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        //xu ly draw menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_Drawer, R.string.close_Drawer);
                drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setupViewPager();

        Menu menu = navigationView.getMenu();
        MenuItem itemLogout = menu.findItem(R.id.menu_logout);
        //xu ly logout
        itemLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                    // Xóa dữ liệu user khỏi Intent hiện tại
                    if(bundle != null){
                        intent.removeExtra("USERNAME_ACCOUNT");
                        intent.removeExtra("ID_ACCOUNT");
                        intent.removeExtra("EMAIL_ACCOUNT");
                        intent.removeExtra("ROLE_ACCOUNT");
                    }
                    // Chuyển về LoginActivity và xóa stack
                    Intent login = new Intent(MainMenuActivity.this, LoginActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                return false;
            }
        });

        //xu ly click
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                viewPager2.setCurrentItem(0);
            } else if (item.getItemId() == R.id.menu_expenses) {
                viewPager2.setCurrentItem(1);
            } else if (item.getItemId() == R.id.menu_budget) {
                viewPager2.setCurrentItem(2);
            } else if (item.getItemId() == R.id.menu_setting) {
                viewPager2.setCurrentItem(3);
            } else {
                viewPager2.setCurrentItem(0);
            }
            return true;
        });
    }

    private void setupViewPager() {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        //xu ly vuot man hinh
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                } else if (position == 1) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_expenses).setChecked(true);
                } else if (position ==2) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
                } else if (position==3) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_setting).setChecked(true);
                } else {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home) {
            viewPager2.setCurrentItem(0);
        } else if (item.getItemId() == R.id.menu_expenses) {
            viewPager2.setCurrentItem(1);
        } else if (item.getItemId() == R.id.menu_budget)  {
            viewPager2.setCurrentItem(2);
        } else if (item.getItemId() == R.id.menu_setting) {
            viewPager2.setCurrentItem(3);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    // Thêm method để truyền thông tin user cho các Activity con
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public String getCurrentEmail() {
        return currentEmail;
    }
    
    public int getCurrentRole() {
        return currentRole;
    }
    
    // Method để truyền thông tin user qua Intent
    public void passUserInfoToActivity(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", currentUserId);
        bundle.putString("USERNAME", currentUsername);
        bundle.putString("EMAIL", currentEmail);
        bundle.putInt("ROLE", currentRole);
        intent.putExtras(bundle);
    }
}
