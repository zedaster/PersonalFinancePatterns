package me.zedaster.financeadminui.component;

import javax.swing.*;

public class ActionLayout implements Component {
    private final JPanel panel;

    public ActionLayout(ActionButton ... buttons) {
        this.panel = new JPanel();
        for (ActionButton button : buttons) {
            this.panel.add(button.toAwtComponent());
        }
    }

    @Override
    public java.awt.Component toAwtComponent() {
        return panel;
    }
}
