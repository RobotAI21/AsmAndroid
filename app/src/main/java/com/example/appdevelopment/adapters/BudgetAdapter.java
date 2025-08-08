package com.example.appdevelopment.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdevelopment.EditBudgetActivity;
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;

import java.text.NumberFormat; // SỬA: Thêm để định dạng số
import java.util.List;
import java.util.Locale; // SỬA: Thêm để định dạng số

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<BudgetModel> budgets;
    public BudgetAdapter(List<BudgetModel> budgets) {
        this.budgets = budgets;
    }

    @NonNull
    @Override
    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_budget,parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetAdapter.BudgetViewHolder holder, int position) {
        BudgetModel budget = budgets.get(position);
        holder.tvName.setText(budget.getNameBudget());
        holder.tvDescription.setText(budget.getDescription());

        // SỬA: Định dạng và hiển thị số tiền còn lại / tổng số tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String remainingMoneyFormatted = formatter.format(budget.getRemainingMoney());
        String totalMoneyFormatted = formatter.format(budget.getMoneyBudget());
        String moneyText = "Remaining: " + remainingMoneyFormatted + " / " + totalMoneyFormatted;
        holder.tvMoney.setText(moneyText);

        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditBudgetActivity.class);
                intent.putExtra("BUDGET_ID", budget.getId());
                intent.putExtra("BUDGET_NAME", budget.getNameBudget());
                intent.putExtra("BUDGET_MONEY", budget.getMoneyBudget());
                intent.putExtra("BUDGET_DESCRIPTION", budget.getDescription());
                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                BudgetRepository repository = new BudgetRepository(v.getContext());
                int rows = repository.deleteBudget(budget.getId());
                if (rows > 0) {
                    budgets.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(v.getContext(), "Delete budget successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Delete budget failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMoney, tvDescription;
        Button btnEdit, btnDelete;
        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBudgetName);
            tvMoney = itemView.findViewById(R.id.tvBudgetMoney);
            tvDescription = itemView.findViewById(R.id.tvBudgetDescription);
            btnEdit = itemView.findViewById(R.id.btnEditBudget);
            btnDelete = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}