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

import java.util.List;

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
        holder.tvMoney.setText(String.valueOf(budget.getMoneyBudget()));
        holder.tvDescription.setText(budget.getDescription());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Check if position is valid
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EditBudgetActivity.class);
                    intent.putExtra("BUDGET_ID", budget.getId());
                    intent.putExtra("BUDGET_NAME", budget.getNameBudget());
                    intent.putExtra("BUDGET_MONEY", budget.getMoneyBudget());
                    intent.putExtra("BUDGET_DESCRIPTION", budget.getDescription());
                    context.startActivity(intent);
                }

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BudgetRepository repository = new BudgetRepository(v.getContext());
                    int rows = repository.deleteBudget(budget.getId());
                    if (rows > 0) {
                        budgets.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(v.getContext(), "Delete budget successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Delete budget failed", Toast.LENGTH_SHORT).show();
                    }
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
