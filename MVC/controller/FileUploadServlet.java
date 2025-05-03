package com.kirini.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.kirini.dao.BoardFileDAO;
import com.kirini.dto.BoardFileDTO;
import com.kirini.util.FileConfigurationProperties;
import com.kirini.util.FileUploadUtil;

/**
 * 파일 업로드를 처리하는 서블릿
 */
@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50,    // 50MB
    location = "/tmp"
)
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BoardFileDAO boardFileDAO = new BoardFileDAO();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUploadServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            // 게시글 ID 가져오기
            int boardId = Integer.parseInt(request.getParameter("boardId"));
            
            // 게시판 타입 가져오기 (news, free, anonymous 등)
            String boardType = request.getParameter("boardType");
            if (boardType == null || boardType.isEmpty()) {
                boardType = "default"; // 기본 값 설정
            }
            
            // 업로드 디렉토리 설정
            String uploadDir = FileConfigurationProperties.getUploadDirByBoardType(boardType);
            
            // 파일 파트 가져오기
            Collection<Part> parts = request.getParts();
            
            // 응답 객체 준비
            StringBuilder resultJson = new StringBuilder();
            resultJson.append("{ \"files\": [");
            
            boolean isFirstFile = true;
            
            for (Part part : parts) {
                // 파일이 아닌 폼 필드는 건너뛰기
                if (part.getSubmittedFileName() == null || part.getSubmittedFileName().isEmpty()) {
                    continue;
                }
                
                // 원본 파일명
                String originalFileName = FileUploadUtil.getFileName(part);
                
                // 허용된 파일 타입 확인
                if (!FileConfigurationProperties.isAllowedFile(originalFileName)) {
                    if (!isFirstFile) {
                        resultJson.append(",");
                    }
                    resultJson.append("{\"fileName\": \"" + originalFileName + "\", \"error\": \"허용되지 않는 파일 타입입니다.\"}");
                    isFirstFile = false;
                    continue;
                }
                
                // 허용된 파일 크기 확인
                if (!FileUploadUtil.isAllowedFileSize(part.getSize(), FileConfigurationProperties.MAX_FILE_SIZE)) {
                    if (!isFirstFile) {
                        resultJson.append(",");
                    }
                    resultJson.append("{\"fileName\": \"" + originalFileName + "\", \"error\": \"파일 크기가 허용 범위를 초과합니다.\"}");
                    isFirstFile = false;
                    continue;
                }
                
                // 파일 업로드 처리
                String savedFilePath = FileUploadUtil.uploadFile(part, uploadDir);
                
                if (savedFilePath != null) {
                    // 파일 정보 DB 저장
                    BoardFileDTO boardFile = new BoardFileDTO();
                    boardFile.setBoardId(boardId);
                    boardFile.setOriginalFileName(originalFileName);
                    boardFile.setSavedFilePath(savedFilePath);
                    boardFile.setFileSize(part.getSize());
                    boardFile.setFileType(part.getContentType());
                    boardFile.setUploadDate(new Date());
                    boardFile.setDeleted(false);
                    
                    int fileId = boardFileDAO.insertBoardFile(boardFile);
                    
                    if (fileId > 0) {
                        if (!isFirstFile) {
                            resultJson.append(",");
                        }
                        resultJson.append("{\"fileId\": " + fileId + ", \"fileName\": \"" + originalFileName + "\", \"filePath\": \"" + savedFilePath + "\", \"fileSize\": " + part.getSize() + "}");
                        isFirstFile = false;
                    }
                }
            }
            
            resultJson.append("]}");
            
            // 클라이언트에 결과 전송
            response.setContentType("application/json");
            response.getWriter().write(resultJson.toString());
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 게시글 ID가 필요합니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 멀티파트 요청이 아닌 일반 요청 처리 (Get 요청 등)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<html><body>");
        response.getWriter().write("<h1>파일 업로드</h1>");
        response.getWriter().write("<p>이 서블릿은 POST 방식의 멀티파트 요청만 처리합니다.</p>");
        response.getWriter().write("<form action=\"upload\" method=\"post\" enctype=\"multipart/form-data\">");
        response.getWriter().write("게시글 ID: <input type=\"text\" name=\"boardId\" required><br>");
        response.getWriter().write("게시판 타입: <select name=\"boardType\">");
        response.getWriter().write("<option value=\"news\">키보드 소식</option>");
        response.getWriter().write("<option value=\"free\">자유게시판</option>");
        response.getWriter().write("<option value=\"anonymous\">익명게시판</option>");
        response.getWriter().write("</select><br>");
        response.getWriter().write("파일 선택: <input type=\"file\" name=\"file\" multiple><br>");
        response.getWriter().write("<input type=\"submit\" value=\"업로드\">");
        response.getWriter().write("</form>");
        response.getWriter().write("</body></html>");
    }
}