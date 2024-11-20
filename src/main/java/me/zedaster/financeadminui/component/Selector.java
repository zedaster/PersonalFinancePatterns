package me.zedaster.financeadminui.component;

import me.zedaster.financeadminui.frame.FrameManager;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selector<T> implements Component {
    private final String title;
    private final JComboBox<String> comboBox;

    private final Map<String, T> valueMap;
    public Selector(FrameManager manager, String title, List<T> values) {
        this.title = title;
        String[] options = values.stream().map(Object::toString).toArray(String[]::new);
        this.comboBox = new JComboBox<>(options);
        this.valueMap = new HashMap<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            this.valueMap.put(options[i], values.get(i));
        }
    }

    public T getValue() {
        String option = (String) this.comboBox.getSelectedItem();
        return valueMap.get(option);
    }

    @Override
    public java.awt.Component toAwtComponent() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(title);
        panel.add(label);
        panel.add(comboBox);
        return panel;
    }
}
