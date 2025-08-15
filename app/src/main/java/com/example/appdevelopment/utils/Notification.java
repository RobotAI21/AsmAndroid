package com.example.appdevelopment.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.appdevelopment.MainMenuActivity;
import com.example.appdevelopment.R;

/**
 * Utility class để quản lý thông báo trong ứng dụng
 * Xử lý các thông báo cảnh báo ngân sách và vượt quá ngân sách
 */
public class Notification {
    private static final String CHANNEL_ID = "budget_alerts";

    /**
     * Phương thức gửi thông báo cảnh báo hoặc vượt ngân sách
     * @param context Context của ứng dụng
     * @param title Tiêu đề thông báo
     * @param message Nội dung thông báo
     * @param notificationId ID của thông báo
     */
    private static void showNotification(Context context, String title, String message, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Hủy thông báo cũ nếu có
        notificationManager.cancel(notificationId);

        // Tạo notification channel cho Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts for budget limits");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        
        // Tạo Intent để mở MainMenuActivity khi nhấn vào thông báo
        Intent intent = new Intent(context, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Kiểm tra quyền gửi thông báo cho Android 13 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Gửi thông báo
        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * Phương thức gửi thông báo cảnh báo khi còn ít ngân sách
     * @param context Context của ứng dụng
     * @param budgetName Tên ngân sách
     * @param remainingAmount Số tiền còn lại
     */
    public static void showBudgetWarning(Context context, String budgetName, int remainingAmount) {
        String notifyKey = "warning_" + budgetName;
        // Kiểm tra xem đã gửi thông báo cho ngân sách này chưa
        if (hasNotified(context, notifyKey)) return;

        String title = "Budget Warning!";
        String message = "You only have" + formatCurrency(remainingAmount) + " in Budget \"" + budgetName + "\".";
        showNotification(context, title, message, 1);
        markAsNotified(context, notifyKey);
    }

    /**
     * Phương thức gửi thông báo khi vượt quá ngân sách
     * @param context Context của ứng dụng
     * @param budgetName Tên ngân sách
     * @param spentAmount Số tiền đã chi tiêu
     * @param budgetLimit Giới hạn ngân sách
     */
    public static void showBudgetExceeded(Context context, String budgetName, int spentAmount, int budgetLimit) {
        String notifyKey = "exceeded_" + budgetName;
        // Kiểm tra xem đã gửi thông báo cho ngân sách này chưa
        if (hasNotified(context, notifyKey)) return;

        String title = "Expense Exceeded!";
        String message = "You had spent" + formatCurrency(spentAmount)
                + ", over budget \"" + budgetName + "\" (" + formatCurrency(budgetLimit) + ").";
        showNotification(context, title, message,1);
        markAsNotified(context, notifyKey);
    }

    /**
     * Phương thức reset thông báo cho một ngân sách
     * Xóa trạng thái đã thông báo để có thể gửi lại
     * @param context Context của ứng dụng
     * @param budgetName Tên ngân sách
     */
    public static void resetBudgetNotifications(Context context, String budgetName) {
        SharedPreferences prefs = context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE);
        prefs.edit()
                .remove("warning_" + budgetName)
                .remove("exceeded_" + budgetName)
                .apply();
    }

    /**
     * Phương thức kiểm tra xem đã gửi thông báo cho key này chưa
     * @param context Context của ứng dụng
     * @param key Key để kiểm tra
     * @return true nếu đã thông báo, false nếu chưa
     */
    private static boolean hasNotified(Context context, String key) {
        return context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE)
                .getBoolean(key, false);
    }

    /**
     * Phương thức đánh dấu đã gửi thông báo cho key này
     * @param context Context của ứng dụng
     * @param key Key để đánh dấu
     */
    private static void markAsNotified(Context context, String key) {
        context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, true)
                .apply();
    }

    /**
     * Phương thức định dạng số tiền theo định dạng tiền tệ Việt Nam
     * @param amount Số tiền cần định dạng
     * @return Chuỗi đã định dạng
     */
    private static String formatCurrency(int amount) {
        return String.format("%,d₫", amount);
    }
}


