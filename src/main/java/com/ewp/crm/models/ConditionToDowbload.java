package com.ewp.crm.models;

import java.util.List;

public class ConditionToDowbload {
    private List<String> selected;

    private String delimeter;

    public List<String> getSelected() {
        return selected;
    }

    public void setSelected(List<String> selected) {
        this.selected = selected;
    }

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }
}