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

/**
 * Adapter cho RecyclerView hiển thị danh sách chi tiêu
 * Quản lý việc hiển thị, chỉnh sửa và xóa chi tiêu
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    // Danh sách chi tiêu cần hiển thị
    private List<ExpenseModel> expenses;
    
    /**
     * Constructor của adapter
     * @param expenses Danh sách chi tiêu
     */
    public  ExpenseAdapter(List<ExpenseModel> expenses) {
        this.expenses = expenses;
    }
    
    /**
     * Phương thức tạo ViewHolder mới
     * @param parent ViewGroup chứa các item
     * @param viewType Loại view
     * @return ExpenseViewHolder mới
     */
    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    /**
     * Phương thức gắn dữ liệu vào ViewHolder
     * @param holder ViewHolder cần gắn dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenses.get(position);
        
        // Hiển thị thông tin chi tiêu
        holder.tvName.setText("Name: " + expense.getName());
        holder.tvMoney.setText("Amount" + expense.getMoney());
        holder.tvDescription.setText("Description: " + expense.getDescription());
        holder.tvDate.setText("Date: " + expense.getCreatedAt());
        
        // Xử lý sự kiện xóa chi tiêu
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Xóa chi tiêu khỏi cơ sở dữ liệu
                    ExpenseRepository repository = new ExpenseRepository(v.getContext());
                    int rows = repository.deleteExpense(expense.getId());
                    if (rows > 0) {
                        // Xóa item khỏi danh sách và cập nhật giao diện
                        expenses.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(v.getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Failed to delete expense", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        
        // Xử lý sự kiện chỉnh sửa chi tiêu
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // Mở Activity chỉnh sửa chi tiêu
                    ExpenseModel exp = expenses.get(pos);
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EditExpenseActivity.class);
                    // Truyền thông tin chi tiêu qua Intent
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

    /**
     * Phương thức trả về số lượng item trong danh sách
     * @return Số lượng chi tiêu
     */
    @Override
    public int getItemCount() {
        return expenses.size();
    }
    
    /**
     * ViewHolder chứa các thành phần UI của mỗi item chi tiêu
     */
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các thành phần UI
        TextView tvName, tvMoney, tvDescription, tvDate;
        Button btnEdit, btnDelete;

        /**
         * Constructor của ViewHolder
         * @param itemView View chứa layout của item
         */
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo các thành phần UI
            tvName = itemView.findViewById(R.id.tvExpenseName);
            tvMoney = itemView.findViewById(R.id.tvExpenseMoney);
            tvDescription = itemView.findViewById(R.id.tvExpenseDescription);
            btnEdit = itemView.findViewById(R.id.btnEditExpense);
            btnDelete = itemView.findViewById(R.id.btnDeleteExpense);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}
