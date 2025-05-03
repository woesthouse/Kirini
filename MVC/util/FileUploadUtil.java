package com.kirini.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

/**
 * 파일 업로드를 위한 유틸리티 클래스
 */
public class FileUploadUtil {
    
    /**
     * Part 객체에서 파일명 추출
     * @param part 업로드된 파일의 Part 객체
     * @return 파일명
     */
    public static String getFileName(Part part) {
        // Content-Disposition 헤더에서 파일명 추출
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        
        return null;
    }
    
    /**
     * 파일 업로드 처리
     * @param part 업로드된 파일의 Part 객체
     * @param uploadDir 업로드 디렉토리 경로
     * @return 저장된 파일의 경로 (실패 시 null 반환)
     */
    public static String uploadFile(Part part, String uploadDir) {
        try {
            if (part == null || part.getSize() == 0) {
                return null;
            }
            
            // 디렉토리가 없으면 생성
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            
            // 파일명 중복 방지를 위한 UUID 사용
            String originalFileName = getFileName(part);
            String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + fileExt;
            
            // 날짜별 폴더 생성
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = sdf.format(new Date());
            
            File dateDir = new File(uploadDir + File.separator + datePath);
            if (!dateDir.exists()) {
                dateDir.mkdirs();
            }
            
            // 파일 저장
            String filePath = datePath + File.separator + savedFileName;
            part.write(uploadDir + File.separator + filePath);
            
            return filePath;
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 파일 삭제
     * @param filePath 삭제할 파일의 절대 경로
     * @return 삭제 성공 여부
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
    
    /**
     * 이미지 파일 여부 확인
     * @param fileName 파일명
     * @return 이미지 파일 여부
     */
    public static boolean isImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || 
               ext.equals("gif") || ext.equals("bmp") || ext.equals("webp");
    }
    
    /**
     * 허용된 파일 확장자 여부 확인
     * @param fileName 파일명
     * @param allowedExtensions 허용된 확장자 배열
     * @return 허용된 파일 확장자 여부
     */
    public static boolean isAllowedFileType(String fileName, String[] allowedExtensions) {
        if (fileName == null || fileName.isEmpty() || allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }
        
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        for (String allowedExt : allowedExtensions) {
            if (ext.equals(allowedExt.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 파일 크기 제한 확인
     * @param fileSize 파일 크기 (바이트)
     * @param maxSize 최대 허용 크기 (바이트)
     * @return 허용 크기 내 여부
     */
    public static boolean isAllowedFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }
    
    /**
     * 파일 크기를 읽기 쉬운 형태로 변환
     * @param size 파일 크기 (바이트)
     * @return 읽기 쉬운 형태의 파일 크기 (예: "1.5 MB")
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        return new java.text.DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}