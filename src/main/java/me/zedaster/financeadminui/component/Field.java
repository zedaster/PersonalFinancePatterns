package me.zedaster.financeadminui.component;

import me.zedaster.financeadminui.frame.FrameManager;

import javax.swing.*;

public class Field implements Component {

    private final String title;

    private final JTextField textField;

    public Field(FrameManager frameManager, String title) {
        this.textField = new JTextField();
        this.title = title;
    }

    public String getValue() {
        return textField.getText();
    }

    @Override
    public java.awt.Component toAwtComponent() {
        JLabel label = new JLabel(title);
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(textField);
        return panel;
    }
}
