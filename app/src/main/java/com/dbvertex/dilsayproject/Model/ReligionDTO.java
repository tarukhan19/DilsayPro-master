package com.dbvertex.dilsayproject.Model;

public class ReligionDTO {

    String religionId, relegionName;
    private boolean isSelected,isChecked = false;

    public ReligionDTO() {
        this.religionId = religionId;
        this.relegionName = relegionName;
    }

    public String getReligionId() {
        return religionId;
    }

    public void setReligionId(String religionId) {
        this.religionId = religionId;
    }

    public String getRelegionName() {
        return relegionName;
    }

    public void setRelegionName(String relegionName) {
        this.relegionName = relegionName;
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
