package com.kirini.dto;

import java.util.Date;

/**
 * 게시글 정보를 담는 DTO 클래스
 */
public class BoardDTO {
    private int boardId;          // 게시글 ID
    private String title;         // 제목
    private String content;       // 내용
    private int userId;           // 작성자 ID
    private String userNickname;  // 작성자 닉네임
    private String boardType;     // 게시판 유형 (공지, 자유, 익명)
    private int viewCount;        // 조회수
    private int likeCount;        // 좋아요 수
    private int commentCount;     // 댓글 수
    private Date createdDate;     // 작성일
    private Date modifiedDate;    // 수정일
    private boolean isDeleted;    // 삭제 여부
    private String fileName;      // 첨부 파일명
    private String filePath;      // 첨부 파일 경로
    
    // 기본 생성자
    public BoardDTO() {
    }

    // Getter와 Setter 메서드
    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
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

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
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
        return "BoardDTO [boardId=" + boardId + ", title=" + title + ", userId=" + userId + ", userNickname="
                + userNickname + ", boardType=" + boardType + ", viewCount=" + viewCount + ", likeCount=" + likeCount
                + ", commentCount=" + commentCount + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
                + ", isDeleted=" + isDeleted + "]";
    }
}