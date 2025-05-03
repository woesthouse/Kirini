package com.kirini.dto;

import java.util.Date;

/**
 * Q&A 정보를 담는 DTO 클래스
 */
public class QnaDTO {
    private int qnaId;            // Q&A ID
    private String title;         // 제목
    private String content;       // 내용
    private int userId;           // 작성자 ID
    private String userNickname;  // 작성자 닉네임
    private Date createdDate;     // 작성일
    private Date modifiedDate;    // 수정일
    private String status;        // 상태 (대기중, 답변완료)
    private String answer;        // 답변 내용
    private int adminId;          // 답변한 관리자 ID
    private String adminNickname; // 답변한 관리자 닉네임
    private Date answeredDate;    // 답변일
    private boolean isPrivate;    // 비공개 여부
    private String category;      // 카테고리 (일반문의, 계정, 기술지원 등)
    private boolean isDeleted;    // 삭제 여부
    private String fileName;      // 첨부 파일명
    private String filePath;      // 첨부 파일 경로
    
    // 기본 생성자
    public QnaDTO() {
    }
    
    // Getter와 Setter 메서드
    public int getQnaId() {
        return qnaId;
    }
    
    public void setQnaId(int qnaId) {
        this.qnaId = qnaId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserNickname() {
        return userNickname;
    }
    
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public Date getModifiedDate() {
        return modifiedDate;
    }
    
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public String getAdminNickname() {
        return adminNickname;
    }
    
    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }
    
    public Date getAnsweredDate() {
        return answeredDate;
    }
    
    public void setAnsweredDate(Date answeredDate) {
        this.answeredDate = answeredDate;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }
    
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    @Override
    public String toString() {
        return "QnaDTO [qnaId=" + qnaId + ", title=" + title + ", userId=" + userId + ", userNickname=" + userNickname
                + ", createdDate=" + createdDate + ", status=" + status + ", adminId=" + adminId + ", adminNickname="
                + adminNickname + ", answeredDate=" + answeredDate + ", isPrivate=" + isPrivate + ", category="
                + category + ", isDeleted=" + isDeleted + "]";
    }
}