package com.dbvertex.dilsayproject.Model;

public class CareerDTO {

    String careerId, careerName;
    private boolean isSelected,isChecked = false;

    public CareerDTO(String careerId, String careerName) {
        this.careerId = careerId;
        this.careerName = careerName;
    }

    public CareerDTO() {

    }

    public String getCareerId() {
        return careerId;
    }

    public void setCareerId(String careerId) {
        this.careerId = careerId;
    }

    public String getCareerName() {
        return careerName;
    }

    public void setCareerName(String careerName) {
        this.careerName = careerName;
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
