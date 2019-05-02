package com.ewp.crm.models;

import java.util.ArrayList;

public class ConditionToDowbload {
    private ArrayList<String> selected;

    private String delimeter;

    public ArrayList<String> getSelected() {
        return selected;
    }

    public void setSelected(ArrayList<String> selected) {
        this.selected = selected;
    }

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }
}