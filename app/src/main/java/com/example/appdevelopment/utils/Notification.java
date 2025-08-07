package com.example.appdevelopment.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.appdevelopment.R;

public class Notification {
    private static final String CHANNEL_ID = "budget_alerts";

    public static void showBudgetWarningNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel nếu cần (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Budget Alerts";
            String description = "Alerts for budget limits";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Android 13+ cần kiểm tra quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Show notification
        notificationManager.notify(1001, builder.build());
    }

    // Thông báo khi vượt quá ngân sách
    public static void showBudgetExceededNotification(Context context, String budgetName, int spentAmount, int budgetLimit) {
        String notifyKey = "warning_" + budgetName;
        if (hasNotified(context, notifyKey)) {
            return; // Đã thông báo rồi thì không gửi lại
        }
        String title = "Chi tiêu vượt ngân sách!";
        String message = "Bạn đã chi tiêu " + formatCurrency(spentAmount) + " vượt quá ngân sách " + formatCurrency(budgetLimit) + ".";
        
        showBudgetWarningNotification(context, title, message);
        markAsNotified(context, notifyKey);
    }

    // Thông báo cảnh báo ngân sách
    public static void showBudgetWarningNotification(Context context, String budgetName, int remainingAmount) {
        String notifyKey = "warning_" + budgetName;
        if (hasNotified(context, notifyKey)) {
            return; // Đã cảnh báo rồi thì không gửi lại
        }
        String title = "Cảnh báo ngân sách!";
        String message = "Bạn chỉ còn " + formatCurrency(remainingAmount) + " trong ngân sách " + budgetName + ".";
        
        showBudgetWarningNotification(context, title, message);
        markAsNotified(context, notifyKey);
    }

    // Format tiền tệ theo định dạng Việt Nam
    private static String formatCurrency(int amount) {
        return String.format("%,d₫", amount);
    }

    private static boolean hasNotified(Context context, String key) {
        return context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE)
                .getBoolean(key, false);
    }

    private static void markAsNotified(Context context, String key) {
        context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, true)
                .apply();
    }

}
