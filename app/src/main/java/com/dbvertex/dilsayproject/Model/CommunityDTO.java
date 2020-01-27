package com.dbvertex.dilsayproject.Model;

public class CommunityDTO {
    String communityId, communityName, selectcommunity;
    private boolean isSelected = false;

    public CommunityDTO(String communityId, String communityName) {
        this.communityId = communityId;
        this.communityName = communityName;

    }

    public CommunityDTO() {

    }

    public String getSelectcommunity() {
        return selectcommunity;
    }

    public void setSelectcommunity(String selectcommunity) {
        this.selectcommunity = selectcommunity;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
