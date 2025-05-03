package com.kirini.util;

/**
 * 파일 업로드 관련 설정 정보를 관리하는 클래스
 */
public class FileConfigurationProperties {
    
    // 업로드 기본 경로 (절대 경로로 설정 필요)
    public static final String UPLOAD_BASE_DIR = "C:/kirini/uploads";
    
    // 최대 파일 크기 (10MB)
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 허용된 이미지 파일 확장자
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    // 허용된 문서 파일 확장자
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
    
    // 허용된 압축 파일 확장자
    public static final String[] ALLOWED_ARCHIVE_EXTENSIONS = {"zip", "rar", "7z"};
    
    // 모든 허용된 파일 확장자
    public static final String[] ALLOWED_ALL_EXTENSIONS;
    
    static {
        int imgLen = ALLOWED_IMAGE_EXTENSIONS.length;
        int docLen = ALLOWED_DOCUMENT_EXTENSIONS.length;
        int archiveLen = ALLOWED_ARCHIVE_EXTENSIONS.length;
        
        ALLOWED_ALL_EXTENSIONS = new String[imgLen + docLen + archiveLen];
        
        System.arraycopy(ALLOWED_IMAGE_EXTENSIONS, 0, ALLOWED_ALL_EXTENSIONS, 0, imgLen);
        System.arraycopy(ALLOWED_DOCUMENT_EXTENSIONS, 0, ALLOWED_ALL_EXTENSIONS, imgLen, docLen);
        System.arraycopy(ALLOWED_ARCHIVE_EXTENSIONS, 0, ALLOWED_ALL_EXTENSIONS, imgLen + docLen, archiveLen);
    }
    
    // 게시판별 업로드 디렉토리
    public static String getUploadDirByBoardType(String boardType) {
        return UPLOAD_BASE_DIR + "/" + boardType;
    }
    
    // 허용된 파일 타입인지 확인
    public static boolean isAllowedFile(String fileName) {
        return FileUploadUtil.isAllowedFileType(fileName, ALLOWED_ALL_EXTENSIONS);
    }
    
    // 허용된 이미지 파일인지 확인
    public static boolean isAllowedImageFile(String fileName) {
        return FileUploadUtil.isAllowedFileType(fileName, ALLOWED_IMAGE_EXTENSIONS);
    }
    
    // 허용된 문서 파일인지 확인
    public static boolean isAllowedDocumentFile(String fileName) {
        return FileUploadUtil.isAllowedFileType(fileName, ALLOWED_DOCUMENT_EXTENSIONS);
    }
    
    // 허용된 압축 파일인지 확인
    public static boolean isAllowedArchiveFile(String fileName) {
        return FileUploadUtil.isAllowedFileType(fileName, ALLOWED_ARCHIVE_EXTENSIONS);
    }
}