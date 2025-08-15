package com.example.appdevelopment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdevelopment.BudgetFragment;
import com.example.appdevelopment.ExpenseFragment;
import com.example.appdevelopment.OverviewFragment;
import com.example.appdevelopment.ProfileFragment;

/**
 * Adapter cho ViewPager2 để quản lý các Fragment chính của ứng dụng
 * Chuyển đổi giữa các màn hình: Tổng quan, Chi tiêu, Ngân sách, Hồ sơ
 */
public class ViewPageAdapter extends FragmentStateAdapter {

    /**
     * Constructor của adapter
     * @param fragmentManager FragmentManager để quản lý các Fragment
     * @param lifecycle Lifecycle của Activity chứa ViewPager
     */
    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * Phương thức tạo Fragment cho mỗi vị trí
     * @param position Vị trí của Fragment (0-3)
     * @return Fragment tương ứng với vị trí
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment tương ứng với vị trí
        switch (position) {
            case 0:
                return new OverviewFragment(); // Màn hình Tổng quan
            case 1:
                return new ExpenseFragment();  // Màn hình Chi tiêu
            case 2:
                return new BudgetFragment();   // Màn hình Ngân sách
            case 3:
                return new ProfileFragment();  // Màn hình Hồ sơ
            default:
                return new OverviewFragment(); // Mặc định là màn hình Tổng quan
        }
    }

    /**
     * Phương thức trả về tổng số Fragment
     * @return Số lượng Fragment (4)
     */
    @Override
    public int getItemCount() {
        // Trả về 4 để khớp với số lượng mục trong bottom navigation
        return 4;
    }
}