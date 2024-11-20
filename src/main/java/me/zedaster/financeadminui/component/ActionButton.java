package me.zedaster.financeadminui.component;

import me.zedaster.financeadminui.frame.FrameEvent;
import me.zedaster.financeadminui.frame.FrameManager;

import javax.swing.*;

public class ActionButton implements Component {
    private final FrameManager frameManager;

    private final JButton button;

    public ActionButton(FrameManager frameManager, String title) {
        this.frameManager = frameManager;
        this.button = new JButton(title);
        this.button.addActionListener(e -> this.frameManager.notify(this, FrameEvent.BUTTON_PUSHED));
    }

    @Override
    public java.awt.Component toAwtComponent() {
        return button;
    }
}
