package com.example.appdevelopment.adapters; // Hoặc package com.example.appdevelopment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdevelopment.BudgetFragment;
import com.example.appdevelopment.ExpenseFragment;
import com.example.appdevelopment.OverviewFragment;
import com.example.appdevelopment.SettingFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    // Constructor này khớp với cách gọi trong MainMenuActivity
    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về đúng fragment cho mỗi vị trí
        switch (position) {
            case 0:
                return new OverviewFragment(); // Màn hình Tổng quan
            case 1:
                return new ExpenseFragment();  // Màn hình Chi tiêu
            case 2:
                return new BudgetFragment();   // Màn hình Ngân sách
            case 3:
                return new SettingFragment();  // Màn hình Cài đặt
            default:
                return new OverviewFragment(); // Mặc định
        }
    }

    @Override
    public int getItemCount() {
        // Phải trả về 4 để khớp với số lượng mục trong menu
        return 4;
    }
}