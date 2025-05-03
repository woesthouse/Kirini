package com.kirini.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kirini.dao.BoardFileDAO;
import com.kirini.dto.BoardFileDTO;
import com.kirini.util.FileConfigurationProperties;

/**
 * 파일 다운로드를 처리하는 서블릿
 */
@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BoardFileDAO boardFileDAO = new BoardFileDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownloadServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 파일 ID 가져오기
        String fileIdParam = request.getParameter("fileId");
        
        if (fileIdParam == null || fileIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "파일 ID가 필요합니다.");
            return;
        }
        
        try {
            int fileId = Integer.parseInt(fileIdParam);
            
            // DB에서 파일 정보 조회
            BoardFileDTO boardFile = boardFileDAO.getBoardFileById(fileId);
            
            if (boardFile == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일이 존재하지 않습니다.");
                return;
            }
            
            // 파일 경로 생성
            String filePath = FileConfigurationProperties.UPLOAD_BASE_DIR + File.separator + boardFile.getSavedFilePath();
            
            // 파일 존재 확인
            File downloadFile = new File(filePath);
            if (!downloadFile.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일이 서버에 존재하지 않습니다.");
                return;
            }
            
            // MIME 타입 설정
            String mimeType = getServletContext().getMimeType(filePath);
            if (mimeType == null) {
                // 기본 MIME 타입 설정
                mimeType = "application/octet-stream";
            }
            
            // 다운로드 설정
            response.setContentType(mimeType);
            response.setContentLength((int) downloadFile.length());
            
            // 파일 다운로드 헤더 설정
            String userAgent = request.getHeader("User-Agent");
            String encodedFileName = encodeFileName(userAgent, boardFile.getOriginalFileName());
            
            // Content-Disposition 헤더 설정 (다운로드 파일명 지정)
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            
            // 캐시 제어 헤더 설정
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 파일 전송
            try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(downloadFile));
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())
            ) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "파일 다운로드 중 오류가 발생했습니다.");
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 파일 ID가 필요합니다.");
        }
    }

    /**
     * 브라우저에 따른 파일명 인코딩 처리
     * @param userAgent 사용자 에이전트 문자열
     * @param fileName 원본 파일명
     * @return 인코딩된 파일명
     */
    private String encodeFileName(String userAgent, String fileName) throws IOException {
        if (userAgent == null) {
            return URLEncoder.encode(fileName, "UTF-8");
        }
        
        userAgent = userAgent.toLowerCase();
        
        // MSIE 또는 Edge 브라우저
        if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1 || userAgent.indexOf("edge") != -1) {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        }
        // Chrome, Firefox 등 기타 브라우저
        else {
            return new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // POST 요청도 GET과 동일하게 처리
        doGet(request, response);
    }
}