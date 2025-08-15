package com.example.appdevelopment.adapters;

import android.app.AlertDialog;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho RecyclerView hiển thị danh sách ngân sách
 * Quản lý việc hiển thị, chỉnh sửa và xóa ngân sách
 */
public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    // Danh sách ngân sách cần hiển thị
    private List<BudgetModel> budgets;
    
    /**
     * Constructor của adapter
     * @param budgets Danh sách ngân sách
     */
    public BudgetAdapter(List<BudgetModel> budgets) {
        this.budgets = budgets;
    }

    /**
     * Phương thức tạo ViewHolder mới
     * @param parent ViewGroup chứa các item
     * @param viewType Loại view
     * @return BudgetViewHolder mới
     */
    @NonNull
    @Override
    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_budget,parent, false);
        return new BudgetViewHolder(view);
    }

    /**
     * Phương thức gắn dữ liệu vào ViewHolder
     * @param holder ViewHolder cần gắn dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull BudgetAdapter.BudgetViewHolder holder, int position) {
        BudgetModel budget = budgets.get(position);
        
        // Hiển thị thông tin ngân sách
        holder.tvName.setText(budget.getNameBudget());
        holder.tvDescription.setText(budget.getDescription());

        // Định dạng và hiển thị số tiền còn lại / tổng số tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String remainingMoneyFormatted = formatter.format(budget.getRemainingMoney());
        String totalMoneyFormatted = formatter.format(budget.getMoneyBudget());
        String moneyText = "Remaining: " + remainingMoneyFormatted + " / " + totalMoneyFormatted;
        holder.tvMoney.setText(moneyText);

        // Xử lý sự kiện chỉnh sửa ngân sách
        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditBudgetActivity.class);
                // Truyền thông tin ngân sách qua Intent
                intent.putExtra("BUDGET_ID", budget.getId());
                intent.putExtra("BUDGET_NAME", budget.getNameBudget());
                intent.putExtra("BUDGET_MONEY", budget.getMoneyBudget());
                intent.putExtra("BUDGET_DESCRIPTION", budget.getDescription());
                context.startActivity(intent);
            }
        });

        // Xử lý sự kiện xóa ngân sách
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showDeleteConfirmationDialog(v.getContext(), budget, pos);
            }
        });
    }

    /**
     * Phương thức hiển thị dialog xác nhận xóa ngân sách
     * @param context Context của ứng dụng
     * @param budget Ngân sách cần xóa
     * @param position Vị trí của ngân sách trong danh sách
     */
    private void showDeleteConfirmationDialog(Context context, BudgetModel budget, int position) {
        BudgetRepository repository = new BudgetRepository(context);
        
        // Kiểm tra xem ngân sách có chứa expense nào không
        if (repository.hasExpenses(budget.getId())) {
            // Nếu có expense, hiển thị dialog cảnh báo
            int expenseCount = repository.getExpenseCount(budget.getId());
            String message = "Ngân sách '" + budget.getNameBudget() + "' có " + expenseCount + " chi tiêu.\n" +
                           "Việc xóa ngân sách này sẽ xóa tất cả chi tiêu liên quan.\n" +
                           "Bạn có chắc chắn muốn xóa không?";
            
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa ngân sách")
                    .setMessage(message)
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        deleteBudget(context, budget, position);
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            // Nếu không có expense, xóa trực tiếp
            deleteBudget(context, budget, position);
        }
    }

    /**
     * Phương thức thực hiện xóa ngân sách
     * @param context Context của ứng dụng
     * @param budget Ngân sách cần xóa
     * @param position Vị trí của ngân sách trong danh sách
     */
    private void deleteBudget(Context context, BudgetModel budget, int position) {
        BudgetRepository repository = new BudgetRepository(context);
        int rows = repository.deleteBudget(budget.getId());
        
        if (rows > 0) {
            // Xóa item khỏi danh sách và cập nhật giao diện
            budgets.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Xóa ngân sách thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Xóa ngân sách thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Phương thức trả về số lượng item trong danh sách
     * @return Số lượng ngân sách
     */
    @Override
    public int getItemCount() {
        return budgets.size();
    }

    /**
     * ViewHolder chứa các thành phần UI của mỗi item ngân sách
     */
    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các thành phần UI
        TextView tvName, tvMoney, tvDescription;
        Button btnEdit, btnDelete;
        
        /**
         * Constructor của ViewHolder
         * @param itemView View chứa layout của item
         */
        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo các thành phần UI
            tvName = itemView.findViewById(R.id.tvBudgetName);
            tvMoney = itemView.findViewById(R.id.tvBudgetMoney);
            tvDescription = itemView.findViewById(R.id.tvBudgetDescription);
            btnEdit = itemView.findViewById(R.id.btnEditBudget);
            btnDelete = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}