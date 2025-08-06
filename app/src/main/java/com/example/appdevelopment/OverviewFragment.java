package com.example.appdevelopment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.Locale;

public class OverviewFragment extends Fragment {

    private PieChart pieChart;
    private TextView tvTotalSpending, tvRemainingBudget;
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;

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

        setupPieChart();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại Fragment này để luôn cập nhật
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        int totalSpending = expenseRepository.getTotalMonthlyExpenses();
        int totalBudget = budgetRepository.getTotalBudgetAmount();
        int remainingBudget = totalBudget - totalSpending;
        ArrayList<PieEntry> chartEntries = expenseRepository.getSpendingByCategoryForCurrentMonth();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvTotalSpending.setText(currencyFormat.format(totalSpending));
        tvRemainingBudget.setText(currencyFormat.format(remainingBudget));

        loadPieChartData(chartEntries);
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
    }

    private void loadPieChartData(ArrayList<PieEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setCenterText("Không có dữ liệu tháng này");
            pieChart.invalidate();
            return;
        }

        pieChart.setCenterText("Chi tiêu tháng này");

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