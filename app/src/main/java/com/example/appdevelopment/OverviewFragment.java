package com.example.appdevelopment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
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
    private Spinner spinnerBudgetOverview; // Spinner để chọn budget
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    private List<BudgetModel> budgetList;

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
        spinnerBudgetOverview = view.findViewById(R.id.spinner_budget_overview); // Thay ID cho đúng

        setupPieChart();
        setupBudgetSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại spinner mỗi khi quay lại, phòng trường hợp có budget mới
        setupBudgetSpinner();
    }

    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getAllBudgets();
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetOverview.setAdapter(adapter);

        spinnerBudgetOverview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BudgetModel selectedBudget = (BudgetModel) parent.getItemAtPosition(position);
                if (selectedBudget != null) {
                    loadDataForBudget(selectedBudget);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pieChart.clear();
                tvTotalSpending.setText("0");
                tvRemainingBudget.setText("0");
            }
        });

        // Tải dữ liệu cho item đầu tiên nếu có
        if (!budgetList.isEmpty()){
            loadDataForBudget(budgetList.get(0));
        } else {
            pieChart.clear();
            pieChart.setCenterText("Vui lòng tạo ngân sách");
            pieChart.invalidate();
            tvTotalSpending.setText("0");
            tvRemainingBudget.setText("0");
        }
    }

    private void loadDataForBudget(BudgetModel budget) {
        int totalSpending = expenseRepository.getTotalMonthlyExpensesByBudget(budget.getId());
        int totalBudget = budget.getMoneyBudget();
        int remainingBudget = totalBudget - totalSpending;
        ArrayList<PieEntry> chartEntries = expenseRepository.getSpendingByCategoryForCurrentMonthByBudget(budget.getId());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvTotalSpending.setText(currencyFormat.format(totalSpending));
        tvRemainingBudget.setText(currencyFormat.format(remainingBudget));

        loadPieChartData(chartEntries, budget.getNameBudget());
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
    }

    private void loadPieChartData(ArrayList<PieEntry> entries, String budgetName) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setCenterText("Không có chi tiêu cho ngân sách này");
            pieChart.invalidate();
            return;
        }

        pieChart.setCenterText(budgetName);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) { colors.add(color); }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) { colors.add(color); }

        PieDataSet dataSet = new PieDataSet(entries, "Phân loại chi tiêu");
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