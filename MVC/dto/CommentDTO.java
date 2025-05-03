package com.kirini.dto;

import java.util.Date;

/**
 * 댓글 정보를 담는 DTO 클래스
 */
public class CommentDTO {
    private int commentId;        // 댓글 ID
    private int boardId;          // 게시글 ID
    private int userId;           // 작성자 ID
    private String userNickname;  // 작성자 닉네임
    private String content;       // 댓글 내용
    private Date createdDate;     // 작성일
    private Date modifiedDate;    // 수정일
    private boolean isDeleted;    // 삭제 여부
    private int likeCount;        // 좋아요 수
    private int parentId;         // 부모 댓글 ID (대댓글인 경우)
    
    // 기본 생성자
    public CommentDTO() {
    }
    
    // Getter와 Setter 메서드
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    
    public int getBoardId() {
        return boardId;
    }
    
    public void setBoardId(int boardId) {
        this.boardId = boardId;
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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
    
    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public int getParentId() {
        return parentId;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    @Override
    public String toString() {
        return "CommentDTO [commentId=" + commentId + ", boardId=" + boardId + ", userId=" + userId + ", userNickname="
                + userNickname + ", content=" + content + ", createdDate=" + createdDate + ", modifiedDate="
                + modifiedDate + ", isDeleted=" + isDeleted + ", likeCount=" + likeCount + ", parentId=" + parentId
                + "]";
    }
}