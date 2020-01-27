package com.dbvertex.dilsayproject.Model;

public class RaisedInDTO {

    String raisedInId, raisedInName;
    private boolean isSelected,isChecked = false;

    public RaisedInDTO(String raisedInId, String raisedInName) {
        this.raisedInId = raisedInId;
        this.raisedInName = raisedInName;
    }

    public RaisedInDTO() {

    }

    public String getRaisedInId() {
        return raisedInId;
    }

    public void setRaisedInId(String raisedInId) {
        this.raisedInId = raisedInId;
    }

    public String getRaisedInName() {
        return raisedInName;
    }

    public void setRaisedInName(String raisedInName) {
        this.raisedInName = raisedInName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
