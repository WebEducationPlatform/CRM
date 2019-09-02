package com.ewp.crm.models;

import java.util.List;
import java.util.Map;

public class OtherInformationInputValues {

    private String hash;
    private List<Map<String, List<Map<String, String>>>> otherInformationInputValues;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<Map<String, List<Map<String, String>>>> getOtherInformationInputValues() {
        return otherInformationInputValues;
    }

    public void setOtherInformationInputValues(List<Map<String, List<Map<String, String>>>> otherInformationInputValues) {
        this.otherInformationInputValues = otherInformationInputValues;
    }
}
