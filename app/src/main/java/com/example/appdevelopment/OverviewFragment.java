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
import java.util.List;
import java.util.Locale;

public class OverviewFragment extends Fragment {
    private PieChart pieChart;
    private TextView tvTotalSpending, tvRemainingBudget;
    private Spinner spinnerBudgetSelection;
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    private List<BudgetModel> budgetList;
    private Button btnAddExpense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expenseRepository = new ExpenseRepository(getContext());
        budgetRepository = new BudgetRepository(getContext());
        pieChart = view.findViewById(R.id.pie_chart);
        tvTotalSpending = view.findViewById(R.id.tv_total_spending);
        tvRemainingBudget = view.findViewById(R.id.tv_remaining_budget);
        spinnerBudgetSelection = view.findViewById(R.id.spinner_budget_overview);
        btnAddExpense = view.findViewById(R.id.stickyButton);

        btnAddExpense.setOnClickListener(v -> {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            int userId = (activity != null) ? activity.getCurrentUserId() : -1;

            // Kiểm tra xem userId có hợp lệ không trước khi chuyển
            if (userId != -1) {
                Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
                // Đặt userId vào intent với một "key" là "USER_ID"
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            } else {
                // Thông báo lỗi nếu không tìm thấy user id
                Toast.makeText(getContext(), "User not found, please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        setupPieChart();
        setupBudgetSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupBudgetSpinner();
    }

    private void setupBudgetSpinner() {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        budgetList = budgetRepository.getBudgetsByUserId(userId);
        
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);

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

    private void loadDataForSelectedBudget(BudgetModel selectedBudget) {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        
        int spending = expenseRepository.getTotalMonthlyExpensesByBudgetAndUser(selectedBudget.getId(), userId);
        int totalBudget = selectedBudget.getMoneyBudget();
        int remainingBudget = totalBudget - spending;
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalSpending.setText(currencyFormat.format(spending));
        tvRemainingBudget.setText(currencyFormat.format(remainingBudget));
        
        // Kiểm tra và thông báo nếu vượt quá ngân sách
        checkBudgetLimitAndNotify(selectedBudget, spending, remainingBudget);
        
        // Hiển thị các expense của budget được chọn
        ArrayList<PieEntry> entries = expenseRepository.getExpensesByBudgetForCurrentMonthByUser(selectedBudget.getId(), userId);
        
        loadPieChartData(entries, selectedBudget.getNameBudget() + " Expenses");
    }

    private void loadPieChartForAllBudgets() {
        MainMenuActivity activity = (MainMenuActivity) getActivity();
        int userId = (activity != null) ? activity.getCurrentUserId() : -1;
        budgetList = budgetRepository.getBudgetsByUserId(userId);
        ArrayList<PieEntry> entries = new ArrayList<>();
        int totalSpending = 0;
        int totalBudget = 0;
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

    private void checkBudgetLimitAndNotify(BudgetModel budget, int spending, int remainingBudget) {
        if (remainingBudget <= 0) {
            // Vượt quá ngân sách
            Notification.showBudgetExceeded(
                getContext(),
                budget.getNameBudget(),
                spending,
                budget.getMoneyBudget()
            );
        } else if (remainingBudget <= budget.getMoneyBudget() * 0.1) {
            // Cảnh báo ngân sách (còn 10%)
            Notification.showBudgetWarning(
                getContext(),
                budget.getNameBudget(),
                remainingBudget
            );
        }
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
    }

    private void loadPieChartData(ArrayList<PieEntry> entries, String title) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setCenterText("No Expenses");
            pieChart.invalidate();
            return;
        }
        pieChart.setCenterText(title);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) { colors.add(color); }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) { colors.add(color); }
        PieDataSet dataSet = new PieDataSet(entries, "Budget");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400);
    }
}