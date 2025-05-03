package com.kirini.dto;

import java.util.Date;

/**
 * 키보드 정보를 담는 DTO 클래스
 */
public class KeyboardDTO {
    private int keyboardId;        // 키보드 ID
    private String name;           // 키보드 이름
    private String brand;          // 브랜드
    private String category;       // 카테고리 (게이밍, 사무용 등)
    private String switchType;     // 스위치 타입 (적축, 갈축, 청축, 흑축 등)
    private String connectionType; // 연결 방식 (유선, 무선, 블루투스 등)
    private String layout;         // 배열 (텐키리스, 풀배열 등)
    private String keycap;         // 키캡 재질
    private String description;    // 상세 설명
    private int price;             // 가격
    private int viewCount;         // 조회수
    private int likeCount;         // 좋아요 수
    private int commentCount;      // 리뷰/코멘트 수
    private Date registeredDate;   // 등록일
    private Date modifiedDate;     // 수정일
    private String imagePath;      // 이미지 경로
    private boolean isDeleted;     // 삭제 여부
    
    // 기본 생성자
    public KeyboardDTO() {
    }
    
    // Getter와 Setter 메서드
    public int getKeyboardId() {
        return keyboardId;
    }
    
    public void setKeyboardId(int keyboardId) {
        this.keyboardId = keyboardId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSwitchType() {
        return switchType;
    }
    
    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }
    
    public String getConnectionType() {
        return connectionType;
    }
    
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
    
    public String getLayout() {
        return layout;
    }
    
    public void setLayout(String layout) {
        this.layout = layout;
    }
    
    public String getKeycap() {
        return keycap;
    }
    
    public void setKeycap(String keycap) {
        this.keycap = keycap;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public Date getRegisteredDate() {
        return registeredDate;
    }
    
    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }
    
    public Date getModifiedDate() {
        return modifiedDate;
    }
    
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "KeyboardDTO [keyboardId=" + keyboardId + ", name=" + name + ", brand=" + brand + ", category="
                + category + ", switchType=" + switchType + ", connectionType=" + connectionType + ", layout=" + layout
                + ", keycap=" + keycap + ", price=" + price + ", viewCount=" + viewCount + ", likeCount=" + likeCount
                + ", commentCount=" + commentCount + ", registeredDate=" + registeredDate + ", modifiedDate="
                + modifiedDate + ", isDeleted=" + isDeleted + "]";
    }
}