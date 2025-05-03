package com.kirini.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.kirini.dto.QnaDTO;
import com.kirini.dto.UserDTO;
import com.kirini.service.QnaService;
import com.kirini.util.FileUploadUtil;

/**
 * Q&A 관련 요청을 처리하는 컨트롤러
 * 질문 목록 조회, 질문 작성, 답변 작성, 수정, 삭제 등 담당
 */
@WebServlet("/qna/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class QnaController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QnaService qnaService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        qnaService = new QnaService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            // Q&A 메인 페이지 (질문 목록)
            String category = request.getParameter("category");
            String answered = request.getParameter("answered");
            
            int page = 1;
            try {
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
            
            // 검색 기능
            String searchType = request.getParameter("searchType");
            String keyword = request.getParameter("keyword");
            
            List<QnaDTO> qnaList = null;
            int totalCount = 0;
            
            // 검색 조건에 따른 질문 목록 조회
            if (searchType != null && keyword != null && !keyword.isEmpty()) {
                // 검색 조건이 있는 경우
                qnaList = qnaService.searchQnas(category, answered, searchType, keyword, page);
                totalCount = qnaService.getTotalSearchCount(category, answered, searchType, keyword);
            } else if (category != null && !category.isEmpty()) {
                // 카테고리별 질문 목록 조회
                qnaList = qnaService.getQnasByCategory(category, answered, page);
                totalCount = qnaService.getTotalCountByCategory(category, answered);
            } else if (answered != null && !answered.isEmpty()) {
                // 답변 여부별 질문 목록 조회
                qnaList = qnaService.getQnasByAnswered("true".equals(answered), page);
                totalCount = qnaService.getTotalCountByAnswered("true".equals(answered));
            } else {
                // 전체 질문 목록 조회
                qnaList = qnaService.getAllQnas(page);
                totalCount = qnaService.getTotalCount();
            }
            
            int totalPages = (int) Math.ceil(totalCount / 10.0); // 페이지당 10개 질문
            
            // 카테고리 목록 조회
            List<String> categories = qnaService.getAllCategories();
            
            request.setAttribute("qnaList", qnaList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("category", category);
            request.setAttribute("answered", answered);
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", keyword);
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/pages/qna.jsp").forward(request, response);
        } else if (path.equals("/view")) {
            // 질문 상세 조회
            int qnaId = Integer.parseInt(request.getParameter("id"));
            
            // 조회수 증가
            qnaService.increaseViewCount(qnaId);
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            request.setAttribute("qna", qna);
            request.getRequestDispatcher("/pages/qna_view.jsp").forward(request, response);
        } else if (path.equals("/write")) {
            // 질문 작성 페이지
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 카테고리 목록 조회
            List<String> categories = qnaService.getAllCategories();
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/pages/qna_write.jsp").forward(request, response);
        } else if (path.equals("/edit")) {
            // 질문 수정 페이지
            int qnaId = Integer.parseInt(request.getParameter("id"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            // 작성자 체크
            if (qna.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
                return;
            }
            
            // 이미 답변이 달린 질문은 수정 불가
            if (qna.isAnswered()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "이미 답변이 달린 질문은 수정할 수 없습니다.");
                return;
            }
            
            // 카테고리 목록 조회
            List<String> categories = qnaService.getAllCategories();
            
            request.setAttribute("qna", qna);
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/pages/qna_edit.jsp").forward(request, response);
        } else if (path.equals("/answer")) {
            // 질문 답변 페이지 (관리자만 접근 가능)
            int qnaId = Integer.parseInt(request.getParameter("id"));
            
            // 로그인 및 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "답변 권한이 없습니다.");
                return;
            }
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            request.setAttribute("qna", qna);
            request.getRequestDispatcher("/pages/qna_answer.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 처리
        String path = request.getPathInfo();
        
        if (path.equals("/write")) {
            // 질문 작성 처리
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String category = request.getParameter("category");
            
            if (title == null || title.trim().isEmpty() || 
                content == null || content.trim().isEmpty() || 
                category == null || category.trim().isEmpty()) {
                
                request.setAttribute("error", "제목, 내용, 카테고리를 모두 입력해주세요.");
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = qnaService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/qna_write.jsp").forward(request, response);
                return;
            }
            
            // 파일 업로드 처리
            String fileName = null;
            String filePath = null;
            
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/uploads/qna");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                fileName = originalFileName;
                String uuid = UUID.randomUUID().toString();
                filePath = "uploads/qna/" + uuid + "_" + fileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + fileName);
            }
            
            // QnaDTO 생성
            QnaDTO qna = new QnaDTO();
            qna.setTitle(title);
            qna.setContent(content);
            qna.setCategory(category);
            qna.setUserId(user.getUserId());
            qna.setUserNickname(user.getNickname());
            qna.setCreatedDate(new Date());
            qna.setFileName(fileName);
            qna.setFilePath(filePath);
            
            // 질문 저장
            int qnaId = qnaService.insertQna(qna);
            
            // 저장 성공 시 상세 페이지로 이동
            if (qnaId > 0) {
                response.sendRedirect(request.getContextPath() + "/qna/view?id=" + qnaId);
            } else {
                request.setAttribute("error", "질문 저장 중 오류가 발생했습니다.");
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = qnaService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/qna_write.jsp").forward(request, response);
            }
        } else if (path.equals("/edit")) {
            // 질문 수정 처리
            int qnaId = Integer.parseInt(request.getParameter("qnaId"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            // 작성자 체크
            if (qna.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
                return;
            }
            
            // 이미 답변이 달린 질문은 수정 불가
            if (qna.isAnswered()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "이미 답변이 달린 질문은 수정할 수 없습니다.");
                return;
            }
            
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String category = request.getParameter("category");
            
            if (title == null || title.trim().isEmpty() || 
                content == null || content.trim().isEmpty() || 
                category == null || category.trim().isEmpty()) {
                
                request.setAttribute("error", "제목, 내용, 카테고리를 모두 입력해주세요.");
                request.setAttribute("qna", qna);
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = qnaService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/qna_edit.jsp").forward(request, response);
                return;
            }
            
            // 파일 업로드 처리
            String fileName = qna.getFileName();
            String filePath = qna.getFilePath();
            
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                // 기존 파일이 있으면 삭제
                if (filePath != null) {
                    File oldFile = new File(getServletContext().getRealPath("/" + filePath));
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                
                String uploadDir = getServletContext().getRealPath("/uploads/qna");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                fileName = originalFileName;
                String uuid = UUID.randomUUID().toString();
                filePath = "uploads/qna/" + uuid + "_" + fileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + fileName);
            }
            
            // 질문 수정
            qna.setTitle(title);
            qna.setContent(content);
            qna.setCategory(category);
            qna.setModifiedDate(new Date());
            qna.setFileName(fileName);
            qna.setFilePath(filePath);
            
            boolean success = qnaService.updateQna(qna);
            
            // 수정 성공 시 상세 페이지로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/qna/view?id=" + qnaId);
            } else {
                request.setAttribute("error", "질문 수정 중 오류가 발생했습니다.");
                request.setAttribute("qna", qna);
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = qnaService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/qna_edit.jsp").forward(request, response);
            }
        } else if (path.equals("/answer")) {
            // 답변 작성 처리 (관리자만 가능)
            int qnaId = Integer.parseInt(request.getParameter("qnaId"));
            String answer = request.getParameter("answer");
            
            // 로그인 및 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "답변 권한이 없습니다.");
                return;
            }
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            if (answer == null || answer.trim().isEmpty()) {
                request.setAttribute("error", "답변 내용을 입력해주세요.");
                request.setAttribute("qna", qna);
                request.getRequestDispatcher("/pages/qna_answer.jsp").forward(request, response);
                return;
            }
            
            // 답변 정보 설정
            qna.setAnswer(answer);
            qna.setAnswerUserId(user.getUserId());
            qna.setAnswerUserNickname(user.getNickname());
            qna.setAnswerDate(new Date());
            qna.setAnswered(true);
            
            boolean success = qnaService.updateAnswer(qna);
            
            // 답변 성공 시 상세 페이지로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/qna/view?id=" + qnaId);
            } else {
                request.setAttribute("error", "답변 저장 중 오류가 발생했습니다.");
                request.setAttribute("qna", qna);
                request.getRequestDispatcher("/pages/qna_answer.jsp").forward(request, response);
            }
        } else if (path.equals("/delete")) {
            // 질문 삭제 처리
            int qnaId = Integer.parseInt(request.getParameter("id"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 질문 정보 가져오기
            QnaDTO qna = qnaService.getQnaById(qnaId);
            
            // 작성자 또는 관리자만 삭제 가능
            if (qna.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "삭제 권한이 없습니다.");
                return;
            }
            
            // 첨부 파일 삭제
            if (qna.getFilePath() != null) {
                File file = new File(getServletContext().getRealPath("/" + qna.getFilePath()));
                if (file.exists()) {
                    file.delete();
                }
            }
            
            // 질문 삭제
            boolean success = qnaService.deleteQna(qnaId);
            
            // 삭제 후 목록으로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/qna");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "질문 삭제 중 오류가 발생했습니다.");
            }
        }
    }
}