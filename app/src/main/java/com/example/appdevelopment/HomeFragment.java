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
 * Fragment trang chủ của ứng dụng
 * Hiển thị thông tin tổng quan về ngân sách của người dùng
 */
public class HomeFragment extends Fragment {

    // Khai báo repository để thao tác với cơ sở dữ liệu ngân sách
    private BudgetRepository budgetRepository;
    
    // Khai báo các thành phần UI
    private TextView tvTotalBudget, tvBudgetCount, tvWelcomeMessage;

    /**
     * Constructor mặc định của Fragment
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Phương thức factory để tạo instance mới của Fragment
     * @return Instance mới của HomeFragment
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    /**
     * Phương thức khởi tạo Fragment
     * Khởi tạo repository để thao tác với cơ sở dữ liệu
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        budgetRepository = new BudgetRepository(getContext());
    }

    /**
     * Phương thức tạo view cho Fragment
     * Thiết lập giao diện và load dữ liệu
     * @param inflater LayoutInflater để inflate layout
     * @param container ViewGroup chứa Fragment
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     * @return View đã được tạo
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Khởi tạo các thành phần UI
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvBudgetCount = view.findViewById(R.id.tvBudgetCount);
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        
        // Load dữ liệu cho người dùng hiện tại
        loadUserData();
        
        return view;
    }
    
    /**
     * Phương thức load dữ liệu người dùng
     * Lấy thông tin ngân sách và hiển thị lên giao diện
     */
    private void loadUserData() {
        try {
            // Lấy thông tin người dùng từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                int userId = activity.getCurrentUserId();
                String username = activity.getCurrentUsername();
                
                // Hiển thị thông tin chào mừng
                tvWelcomeMessage.setText("Welcome, " + username + "!");
                
                // Lấy danh sách ngân sách của người dùng này
                List<BudgetModel> userBudgets = budgetRepository.getBudgetsByUserId(userId);
                
                // Tính tổng ngân sách
                int totalBudget = 0;
                for (BudgetModel budget : userBudgets) {
                    totalBudget += budget.getMoneyBudget();
                }
                
                // Hiển thị thông tin tổng quan
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