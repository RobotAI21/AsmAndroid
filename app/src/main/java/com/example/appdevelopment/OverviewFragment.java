package com.example.appdevelopment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appdevelopment.budgets.CreateExpenseActivity;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
import com.example.appdevelopment.utils.Notification;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment tổng quan về ngân sách và chi tiêu
 * Hiển thị biểu đồ tròn và thống kê chi tiêu theo ngân sách
 */
public class OverviewFragment extends Fragment {
    // Khai báo các thành phần UI
    private PieChart pieChart;
    private TextView tvTotalSpending, tvRemainingBudget;
    private Spinner spinnerBudgetSelection;
    private Button btnAddExpense;
    
    // Khai báo các repository để thao tác với cơ sở dữ liệu
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    
    // Danh sách ngân sách
    private List<BudgetModel> budgetList;

    /**
     * Phương thức tạo view cho Fragment
     * @param inflater LayoutInflater để inflate layout
     * @param container ViewGroup chứa Fragment
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     * @return View đã được tạo
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    /**
     * Phương thức được gọi sau khi view được tạo
     * Thiết lập giao diện và xử lý các sự kiện
     * @param view View đã được tạo
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các repository
        expenseRepository = new ExpenseRepository(getContext());
        budgetRepository = new BudgetRepository(getContext());
        
        // Khởi tạo các thành phần UI
        pieChart = view.findViewById(R.id.pie_chart);
        tvTotalSpending = view.findViewById(R.id.tv_total_spending);
        tvRemainingBudget = view.findViewById(R.id.tv_remaining_budget);
        spinnerBudgetSelection = view.findViewById(R.id.spinner_budget_overview);
        btnAddExpense = view.findViewById(R.id.stickyButton);

        // Xử lý sự kiện thêm chi tiêu mới
        btnAddExpense.setOnClickListener(v -> {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            int userId = (activity != null) ? activity.getCurrentUserId() : -1;

            // Kiểm tra xem userId có hợp lệ không trước khi chuyển
            if (userId != -1) {
                Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
                // Đặt userId vào intent
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            } else {
                // Thông báo lỗi nếu không tìm thấy user id
                Toast.makeText(getContext(), "User not found, please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập biểu đồ và spinner
        setupPieChart();
        setupBudgetSpinner();
    }

    /**
     * Phương thức được gọi khi Fragment được resume
     * Load lại dữ liệu và cập nhật giao diện
     */
    @Override
    public void onResume() {
        if (spinnerBudgetSelection.getAdapter() != null && !spinnerBudgetSelection.getAdapter().isEmpty()) {
            BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
            if (selectedBudget != null) {
                loadDataForSelectedBudget(selectedBudget);
            }
        }
        super.onResume();
        setupBudgetSpinner();
    }

    /**
     * Phương thức thiết lập spinner chọn ngân sách
     * Tạo adapter và xử lý sự kiện chọn ngân sách
     */
    private void setupBudgetSpinner() {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        budgetList = budgetRepository.getBudgetsByUserId(userId);
        
        // Tạo adapter cho spinner
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);

        // Xử lý sự kiện chọn item trong spinner
        spinnerBudgetSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BudgetModel selectedBudget = (BudgetModel) parent.getItemAtPosition(position);
                if (selectedBudget != null) {
                    loadDataForSelectedBudget(selectedBudget);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadPieChartForAllBudgets();
            }
        });

        // Tải dữ liệu cho item đầu tiên nếu có
        if (!budgetList.isEmpty()) {
            loadDataForSelectedBudget(budgetList.get(0));
        } else {
            loadPieChartForAllBudgets();
        }
    }

    /**
     * Phương thức load dữ liệu cho ngân sách được chọn
     * Tính toán chi tiêu và hiển thị biểu đồ
     * @param selectedBudget Ngân sách được chọn
     */
    private void loadDataForSelectedBudget(BudgetModel selectedBudget) {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        
        // Tính toán chi tiêu và ngân sách còn lại
        int spending = expenseRepository.getTotalMonthlyExpensesByBudgetAndUser(selectedBudget.getId(), userId);
        int totalBudget = selectedBudget.getMoneyBudget();
        int remainingBudget = totalBudget - spending;
        
        // Hiển thị thông tin với định dạng tiền tệ
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalSpending.setText(currencyFormat.format(spending));
        tvRemainingBudget.setText(currencyFormat.format(remainingBudget));
        
        // Kiểm tra và thông báo nếu vượt quá ngân sách
        checkBudgetLimitAndNotify(selectedBudget, spending, remainingBudget);
        
        // Hiển thị các chi tiêu của ngân sách được chọn
        ArrayList<PieEntry> entries = expenseRepository.getExpensesByBudgetForCurrentMonthByUser(selectedBudget.getId(), userId);
        
        loadPieChartData(entries, selectedBudget.getNameBudget() + " Expenses");
    }

    /**
     * Phương thức load biểu đồ cho tất cả ngân sách
     * Hiển thị tổng quan chi tiêu của tất cả ngân sách
     */
    private void loadPieChartForAllBudgets() {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        budgetList = budgetRepository.getBudgetsByUserId(userId);
        ArrayList<PieEntry> entries = new ArrayList<>();
        int totalSpending = 0;
        int totalBudget = 0;
        
        // Tính tổng chi tiêu và ngân sách
        for (BudgetModel budget : budgetList) {
            int spending = expenseRepository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), userId);
            if (spending > 0) {
                entries.add(new PieEntry(spending, budget.getNameBudget()));
            }
            totalSpending += spending;
            totalBudget += budget.getMoneyBudget();
        }
        
        int remainingBudget = totalBudget - totalSpending;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalSpending.setText(currencyFormat.format(totalSpending));
        tvRemainingBudget.setText(currencyFormat.format(remainingBudget));
        loadPieChartData(entries, "All Budgets");
    }

    /**
     * Phương thức kiểm tra giới hạn ngân sách và gửi thông báo
     * @param budget Ngân sách cần kiểm tra
     * @param spending Số tiền đã chi tiêu
     * @param remainingBudget Số tiền còn lại
     */
    private void checkBudgetLimitAndNotify(BudgetModel budget, int spending, int remainingBudget) {
        if (getContext() == null) return; // Đảm bảo an toàn

        // Kiểm tra nếu vượt quá ngân sách
        if (remainingBudget <= 0) {
            Notification.showBudgetExceeded(
                    getContext(),
                    budget.getNameBudget(),
                    spending,
                    budget.getMoneyBudget()
            );
        }
        // Kiểm tra nếu còn ít hơn 10% ngân sách
        else if (remainingBudget <= budget.getMoneyBudget() * 0.1) {
            Notification.showBudgetWarning(
                    getContext(),
                    budget.getNameBudget(),
                    remainingBudget
            );
        }
    }

    /**
     * Phương thức thiết lập biểu đồ tròn
     * Cấu hình các thuộc tính hiển thị của biểu đồ
     */
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
    }

    /**
     * Phương thức load dữ liệu cho biểu đồ tròn
     * @param entries Danh sách dữ liệu để hiển thị
     * @param title Tiêu đề của biểu đồ
     */
    private void loadPieChartData(ArrayList<PieEntry> entries, String title) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setCenterText("No Expenses");
            pieChart.invalidate();
            return;
        }
        
        pieChart.setCenterText(title);
        
        // Tạo danh sách màu sắc cho biểu đồ
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) { colors.add(color); }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) { colors.add(color); }
        
        // Tạo dataset và cấu hình
        PieDataSet dataSet = new PieDataSet(entries, "Budget");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        
        // Tạo dữ liệu và cấu hình biểu đồ
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400);
    }
}