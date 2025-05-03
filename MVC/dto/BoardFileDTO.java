package com.kirini.dto;

import java.util.Date;

/**
 * 게시판 첨부 파일 정보를 담는 DTO 클래스
 */
public class BoardFileDTO {
    private int fileId;          // 파일 ID
    private int boardId;         // 게시글 ID
    private String originalFileName; // 원본 파일명
    private String savedFilePath;    // 저장된 파일 경로
    private long fileSize;       // 파일 크기
    private String fileType;     // 파일 타입(MIME)
    private Date uploadDate;     // 업로드 일시
    private boolean isDeleted;   // 삭제 여부

    // 기본 생성자
    public BoardFileDTO() {
    }

    // 모든 필드를 인자로 받는 생성자
    public BoardFileDTO(int fileId, int boardId, String originalFileName, String savedFilePath, 
                      long fileSize, String fileType, Date uploadDate, boolean isDeleted) {
        this.fileId = fileId;
        this.boardId = boardId;
        this.originalFileName = originalFileName;
        this.savedFilePath = savedFilePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
        this.isDeleted = isDeleted;
    }

    // Getter와 Setter
    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getSavedFilePath() {
        return savedFilePath;
    }

    public void setSavedFilePath(String savedFilePath) {
        this.savedFilePath = savedFilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "BoardFileDTO{" +
                "fileId=" + fileId +
                ", boardId=" + boardId +
                ", originalFileName='" + originalFileName + '\'' +
                ", savedFilePath='" + savedFilePath + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", uploadDate=" + uploadDate +
                ", isDeleted=" + isDeleted +
                '}';
    }
}