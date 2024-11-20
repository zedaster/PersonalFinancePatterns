package me.zedaster.financeadminui.frame;

import me.zedaster.financeadminui.component.Component;

public interface FrameManager {
    void notify(Component sender, FrameEvent event);
}
