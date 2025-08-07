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

import com.example.appdevelopment.EditExpenseActivity;
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.ExpenseModel;
import com.example.appdevelopment.database.ExpenseRepository;


import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<ExpenseModel> expenses;
    public  ExpenseAdapter(List<ExpenseModel> expenses) {
        this.expenses = expenses;
    }
    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenses.get(position);
        holder.tvName.setText("Name: " + expense.getName());
        holder.tvMoney.setText("Amount" + expense.getMoney());
        holder.tvDescription.setText("Description: " + expense.getDescription());
        holder.tvDate.setText("Date: " + expense.getCreatedAt());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Handle delete action, e.g., remove from database and notify adapter
                    ExpenseRepository repository = new ExpenseRepository(v.getContext());
                    int rows = repository.deleteExpense(expense.getId());
                    if (rows > 0) {
                        expenses.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(v.getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Failed to delete expense", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Handle edit action, e.g., open an edit activity
                    ExpenseModel exp = expenses.get(pos);
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EditExpenseActivity.class);
                    intent.putExtra("expense_id", exp.getId());
                    intent.putExtra("expense_name", exp.getName());
                    intent.putExtra("expense_money", exp.getMoney());
                    intent.putExtra("expense_description", exp.getDescription());
                    intent.putExtra("expense_budget_id", exp.getBudgetId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMoney, tvDescription, tvDate;
        Button btnEdit, btnDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExpenseName);
            tvMoney = itemView.findViewById(R.id.tvExpenseMoney);
            tvDescription = itemView.findViewById(R.id.tvExpenseDescription);
            btnEdit = itemView.findViewById(R.id.btnEditExpense);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}
