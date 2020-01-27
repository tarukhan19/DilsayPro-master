package com.dbvertex.dilsayproject.Model;

public class HeightDTO {
    String heightId, heightName;
    private boolean isSelected,isChecked = false;

    public HeightDTO(String heightId, String heightName) {
        this.heightId = heightId;
        this.heightName = heightName;
    }

    public HeightDTO() {

    }

    public String getHeightId() {
        return heightId;
    }

    public void setHeightId(String heightId) {
        this.heightId = heightId;
    }

    public String getHeightName() {
        return heightName;
    }

    public void setHeightName(String heightName) {
        this.heightName = heightName;
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
