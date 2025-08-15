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

/**
 * Activity chính của ứng dụng quản lý ngân sách
 * Chứa navigation drawer, bottom navigation và ViewPager để chuyển đổi giữa các fragment
 */
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo các thành phần UI
    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    // Khai báo các biến để lưu trữ dữ liệu
    Intent intent;
    Bundle bundle;
    TextView tvUsername;
    
    // Biến toàn cục để lưu thông tin người dùng hiện tại
    private int currentUserId;
    private String currentUsername;
    private String currentEmail;
    private int currentRole;
    
    /**
     * Phương thức khởi tạo Activity
     * Thiết lập giao diện, navigation và xử lý các sự kiện
     * @param savedInstanceState Bundle chứa trạng thái trước đó của Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        // Khởi tạo các thành phần UI
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager2 = findViewById(R.id.viewPager);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        
        // Lấy dữ liệu từ Intent
        intent = getIntent();
        bundle = intent.getExtras();
        
        // Kiểm tra và lưu thông tin người dùng
        if (bundle != null){
            currentUserId = bundle.getInt("ID_ACCOUNT", -1);
            currentUsername = bundle.getString("USERNAME_ACCOUNT", "");
            currentEmail = bundle.getString("EMAIL_ACCOUNT", "");
            currentRole = bundle.getInt("ROLE_ACCOUNT", 0);
        } else {
            // Không có dữ liệu user, quay về LoginActivity
            Intent loginIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Thiết lập navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_Drawer, R.string.close_Drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        
        // Thiết lập ViewPager
        setupViewPager();

        // Xử lý sự kiện đăng xuất
        Menu menu = navigationView.getMenu();
        MenuItem itemLogout = menu.findItem(R.id.menu_logout);
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

        // Xử lý sự kiện bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                viewPager2.setCurrentItem(0);
            } else if (item.getItemId() == R.id.menu_expenses) {
                viewPager2.setCurrentItem(1);
            } else if (item.getItemId() == R.id.menu_budget) {
                viewPager2.setCurrentItem(2);
            } else if (item.getItemId() == R.id.menu_profile) {
                viewPager2.setCurrentItem(3);
            } else {
                viewPager2.setCurrentItem(0);
            }
            return true;
        });
    }

    /**
     * Phương thức thiết lập ViewPager
     * Tạo adapter và xử lý sự kiện chuyển trang
     */
    private void setupViewPager() {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        
        // Xử lý sự kiện vuốt màn hình
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Cập nhật trạng thái selected của bottom navigation
                if (position == 0) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                } else if (position == 1) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_expenses).setChecked(true);
                } else if (position ==2) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
                } else if (position==3) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
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

    /**
     * Phương thức xử lý sự kiện chọn item trong navigation drawer
     * @param item MenuItem được chọn
     * @return true nếu xử lý thành công
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_home) {
            viewPager2.setCurrentItem(0);
        } else if (item.getItemId() == R.id.menu_expenses) {
            viewPager2.setCurrentItem(1);
        } else if (item.getItemId() == R.id.menu_budget)  {
            viewPager2.setCurrentItem(2);
        } else if (item.getItemId() == R.id.menu_profile) {
            viewPager2.setCurrentItem(3);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    /**
     * Phương thức lấy ID người dùng hiện tại
     * @return ID người dùng
     */
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Phương thức lấy tên người dùng hiện tại
     * @return Tên người dùng
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Phương thức lấy email người dùng hiện tại
     * @return Email người dùng
     */
    public String getCurrentEmail() {
        return currentEmail;
    }
    
    /**
     * Phương thức lấy vai trò người dùng hiện tại
     * @return Vai trò người dùng
     */
    public int getCurrentRole() {
        return currentRole;
    }
    
    /**
     * Phương thức truyền thông tin người dùng qua Intent
     * @param intent Intent cần truyền thông tin
     */
    public void passUserInfoToActivity(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", currentUserId);
        bundle.putString("USERNAME", currentUsername);
        bundle.putString("EMAIL", currentEmail);
        bundle.putInt("ROLE", currentRole);
        intent.putExtras(bundle);
    }
}
