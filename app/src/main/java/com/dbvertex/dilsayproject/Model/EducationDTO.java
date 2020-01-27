package com.dbvertex.dilsayproject.Model;

public class EducationDTO {
    String educationId, educationName;
    private boolean isSelected,isChecked = false;

    public EducationDTO(String educationId, String educationName) {
        this.educationId = educationId;
        this.educationName = educationName;
    }

    public EducationDTO() {

    }

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
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
