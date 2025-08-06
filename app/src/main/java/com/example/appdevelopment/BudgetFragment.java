package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.appdevelopment.adapters.BudgetAdapter;
import com.example.appdevelopment.budgets.CreateBudgetActivity;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import java.util.List;

public class BudgetFragment extends Fragment {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private BudgetRepository repository;
    private List<BudgetModel> budgets;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        Button btnCreate = view.findViewById(R.id.btnCreateBudget);
        recyclerView = view.findViewById(R.id.rvBudget); // Gán recyclerView ở đây

        repository = new BudgetRepository(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Tải và làm mới dữ liệu
    private void loadBudgets() {
        if (repository != null && recyclerView != null) {
            budgets = repository.getAllBudgets();
            adapter = new BudgetAdapter(budgets);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Gọi hàm loadBudgets() mỗi khi fragment được hiển thị lại
        loadBudgets();
    }
}