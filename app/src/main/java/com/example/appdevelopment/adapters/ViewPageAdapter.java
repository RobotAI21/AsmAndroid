package com.example.appdevelopment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdevelopment.BudgetFragment;
import com.example.appdevelopment.ExpenseFragment;
import com.example.appdevelopment.HomeFragment;
import com.example.appdevelopment.SettingFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    public ViewPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new HomeFragment();
        } else if (position ==1 ) {
            return new ExpenseFragment();
        } else if (position==2) {
            return new BudgetFragment();
        } else if (position==3) {
            return new SettingFragment();
        } else {
            return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
