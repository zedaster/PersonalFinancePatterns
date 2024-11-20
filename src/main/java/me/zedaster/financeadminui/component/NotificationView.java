package me.zedaster.financeadminui.component;

import javax.swing.*;

public class NotificationView {
    public void showNotification(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Уведомление",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
