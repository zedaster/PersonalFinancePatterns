package me.zedaster.financeadminui.frame;

import me.zedaster.financeadminui.component.Component;

import javax.swing.*;
import java.awt.*;

public class FormFrame {
    private final JFrame frame;
    public FormFrame(FrameManager frameManager, String title, Component... components) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Component component : components) {
            formPanel.add(component.toAwtComponent());
        }
    }

    public void setVisible(boolean visibility) {
        this.frame.setVisible(visibility);
    }
}
