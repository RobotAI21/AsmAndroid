package com.example.appdevelopment.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.appdevelopment.R;

public class Notification {
    private static final String CHANNEL_ID = "budget_alerts";

    // Gửi thông báo cảnh báo hoặc vượt ngân sách
    private static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

    // Cảnh báo khi còn ít ngân sách
    public static void showBudgetWarning(Context context, String budgetName, int remainingAmount) {
        String notifyKey = "warning_" + budgetName;
        if (hasNotified(context, notifyKey)) return;

        String title = "Budget Warning!";
        String message = "You only have" + formatCurrency(remainingAmount) + " in Budget \"" + budgetName + "\".";
        showNotification(context, title, message);
        markAsNotified(context, notifyKey);
    }

    // Cảnh báo khi vượt ngân sách
    public static void showBudgetExceeded(Context context, String budgetName, int spentAmount, int budgetLimit) {
        String notifyKey = "exceeded_" + budgetName;
        if (hasNotified(context, notifyKey)) return;

        String title = "Expense Exceeded!";
        String message = "You had spent" + formatCurrency(spentAmount)
                + ", over budget \"" + budgetName + "\" (" + formatCurrency(budgetLimit) + ").";
        showNotification(context, title, message);
        markAsNotified(context, notifyKey);
    }


    public static void resetBudgetNotifications(Context context, String budgetName) {
        SharedPreferences prefs = context.getSharedPreferences("BudgetNotifications", Context.MODE_PRIVATE);
        prefs.edit()
                .remove("warning_" + budgetName)
                .remove("exceeded_" + budgetName)
                .apply();
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

    private static String formatCurrency(int amount) {
        return String.format("%,d₫", amount);
    }
}


