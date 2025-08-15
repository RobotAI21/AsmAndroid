# Tính năng Xóa Budget với Xác nhận

## Mô tả
Tính năng này cho phép xóa budget một cách an toàn với thông báo xác nhận khi budget có chứa expense.

## Các thay đổi đã thực hiện

### 1. BudgetRepository.java
Thêm các phương thức mới:

#### `hasExpenses(int budgetId)`
- **Mục đích**: Kiểm tra xem budget có chứa expense nào không
- **Tham số**: `budgetId` - ID của budget cần kiểm tra
- **Trả về**: `true` nếu có expense, `false` nếu không có

#### `getExpenseCount(int budgetId)`
- **Mục đích**: Đếm số lượng expense của một budget
- **Tham số**: `budgetId` - ID của budget
- **Trả về**: Số lượng expense

#### `deleteBudget(int id)` (đã cập nhật)
- **Mục đích**: Xóa budget và tất cả expense liên quan
- **Cải tiến**: Sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu
- **Quy trình**:
  1. Xóa tất cả expense liên quan trước
  2. Sau đó xóa budget
  3. Commit transaction nếu thành công

### 2. BudgetAdapter.java
Thêm các phương thức mới:

#### `showDeleteConfirmationDialog(Context context, BudgetModel budget, int position)`
- **Mục đích**: Hiển thị dialog xác nhận xóa budget
- **Logic**:
  - Kiểm tra budget có expense không
  - Nếu có: hiển thị dialog cảnh báo với số lượng expense
  - Nếu không: xóa trực tiếp

#### `deleteBudget(Context context, BudgetModel budget, int position)`
- **Mục đích**: Thực hiện xóa budget
- **Quy trình**:
  1. Gọi repository để xóa
  2. Cập nhật UI nếu thành công
  3. Hiển thị thông báo kết quả

## Cách hoạt động

### Khi người dùng nhấn nút Xóa:

1. **Kiểm tra expense**: Hệ thống kiểm tra xem budget có chứa expense nào không
2. **Hiển thị dialog**:
   - **Nếu có expense**: Hiển thị dialog cảnh báo với thông tin chi tiết
   - **Nếu không có expense**: Xóa trực tiếp
3. **Xác nhận xóa**: Người dùng chọn "Xóa" hoặc "Hủy"
4. **Thực hiện xóa**: Nếu xác nhận, hệ thống xóa budget và tất cả expense liên quan
5. **Cập nhật UI**: Refresh danh sách budget

### Dialog xác nhận:
```
Tiêu đề: "Xác nhận xóa ngân sách"
Nội dung: "Ngân sách 'Tên Budget' có X chi tiêu.
         Việc xóa ngân sách này sẽ xóa tất cả chi tiêu liên quan.
         Bạn có chắc chắn muốn xóa không?"
Nút: "Xóa" | "Hủy"
```

## Lợi ích

1. **An toàn dữ liệu**: Ngăn chặn việc xóa nhầm budget có chứa expense
2. **Thông tin rõ ràng**: Người dùng biết chính xác số lượng expense sẽ bị xóa
3. **Tính toàn vẹn**: Sử dụng transaction đảm bảo dữ liệu không bị mất mát
4. **Trải nghiệm người dùng**: Giao diện thân thiện với dialog xác nhận

## Test

Sử dụng class `TestBudgetDelete` để test chức năng:

```java
TestBudgetDelete test = new TestBudgetDelete(context);

// Test kiểm tra expense
test.testCheckBudgetExpenses(budgetId);

// Test xóa budget
test.testDeleteBudget(budgetId);

// Demo đầy đủ quy trình
test.demoBudgetDeletion(budgetId);
```

## Lưu ý

- Tính năng này sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu
- Tất cả expense liên quan sẽ bị xóa khi xóa budget
- Dialog sử dụng AlertDialog với icon cảnh báo
- Thông báo được hiển thị bằng tiếng Việt
