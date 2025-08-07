package com.example.appdevelopment;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private BudgetRepository budgetRepository;
    private TextView tvTotalBudget, tvBudgetCount, tvWelcomeMessage;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        budgetRepository = new BudgetRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Khởi tạo các view
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvBudgetCount = view.findViewById(R.id.tvBudgetCount);
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        
        // Load dữ liệu cho user hiện tại
        loadUserData();
        
        return view;
    }
    
    private void loadUserData() {
        try {
            // Lấy userId từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                int userId = activity.getCurrentUserId();
                String username = activity.getCurrentUsername();
                
                // Hiển thị thông tin chào mừng
                tvWelcomeMessage.setText("Welcome, " + username + "!");
                
                // Lấy danh sách budget của user này
                List<BudgetModel> userBudgets = budgetRepository.getBudgetsByUserId(userId);
                
                // Tính tổng budget
                int totalBudget = 0;
                for (BudgetModel budget : userBudgets) {
                    totalBudget += budget.getMoneyBudget();
                }
                
                // Hiển thị thông tin
                tvTotalBudget.setText("Total Budget: $" + totalBudget);
                tvBudgetCount.setText("Budget Count: " + userBudgets.size());
                
            } else {
                Toast.makeText(getContext(), "Activity not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}