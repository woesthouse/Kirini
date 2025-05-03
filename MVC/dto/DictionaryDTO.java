package com.kirini.dto;

import java.util.Date;

/**
 * 키보드 용어집 정보를 담는 DTO 클래스
 */
public class DictionaryDTO {
    private int termId;           // 용어 ID
    private String term;          // 용어명
    private String definition;    // 용어 정의
    private String category;      // 카테고리 (스위치, 키캡, 배열 등)
    private String imagePath;     // 이미지 경로
    private Date registeredDate;  // 등록일
    private Date modifiedDate;    // 수정일
    private int viewCount;        // 조회수
    private int adminId;          // 등록/수정한 관리자 ID
    private boolean isDeleted;    // 삭제 여부
    
    // 기본 생성자
    public DictionaryDTO() {
    }
    
    // Getter와 Setter 메서드
    public int getTermId() {
        return termId;
    }
    
    public void setTermId(int termId) {
        this.termId = termId;
    }
    
    public String getTerm() {
        return term;
    }
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    public String getDefinition() {
        return definition;
    }
    
    public void setDefinition(String definition) {
        this.definition = definition;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "DictionaryDTO [termId=" + termId + ", term=" + term + ", category=" + category + ", viewCount="
                + viewCount + ", registeredDate=" + registeredDate + ", modifiedDate=" + modifiedDate + ", isDeleted="
                + isDeleted + "]";
    }
}