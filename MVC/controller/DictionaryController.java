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

import com.kirini.dto.DictionaryDTO;
import com.kirini.dto.UserDTO;
import com.kirini.service.DictionaryService;
import com.kirini.util.FileUploadUtil;

/**
 * 키보드 용어사전 관련 요청을 처리하는 컨트롤러
 */
@WebServlet("/dictionary/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class DictionaryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DictionaryService dictionaryService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        dictionaryService = new DictionaryService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            // 용어집 메인 페이지 (용어 목록)
            String category = request.getParameter("category");
            
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
            
            List<DictionaryDTO> termList = null;
            int totalCount = 0;
            
            if (searchType != null && keyword != null && !keyword.isEmpty()) {
                // 검색 조건이 있는 경우
                termList = dictionaryService.searchTerms(category, searchType, keyword, page);
                totalCount = dictionaryService.getTotalSearchCount(category, searchType, keyword);
            } else if (category != null && !category.isEmpty()) {
                // 카테고리별 목록 조회
                termList = dictionaryService.getTermsByCategory(category, page);
                totalCount = dictionaryService.getTotalCountByCategory(category);
            } else {
                // 전체 목록 조회
                termList = dictionaryService.getAllTerms(page);
                totalCount = dictionaryService.getTotalCount();
            }
            
            int totalPages = (int) Math.ceil(totalCount / 20.0); // 페이지당 20개 용어
            
            request.setAttribute("termList", termList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("category", category);
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", keyword);
            
            // 카테고리 목록도 함께 전달
            List<String> categories = dictionaryService.getAllCategories();
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/pages/dictionary.jsp").forward(request, response);
        } else if (path.equals("/view")) {
            // 용어 상세 조회
            int termId = Integer.parseInt(request.getParameter("id"));
            
            // 조회수 증가
            dictionaryService.increaseViewCount(termId);
            
            // 용어 정보 가져오기
            DictionaryDTO term = dictionaryService.getTermById(termId);
            
            request.setAttribute("term", term);
            request.getRequestDispatcher("/pages/dictionary_view.jsp").forward(request, response);
        } else if (path.equals("/write")) {
            // 용어 등록 페이지
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            // 카테고리 목록 가져오기
            List<String> categories = dictionaryService.getAllCategories();
            request.setAttribute("categories", categories);
            
            request.getRequestDispatcher("/pages/dictionary_write.jsp").forward(request, response);
        } else if (path.equals("/edit")) {
            // 용어 수정 페이지
            int termId = Integer.parseInt(request.getParameter("id"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            // 용어 정보 가져오기
            DictionaryDTO term = dictionaryService.getTermById(termId);
            
            // 카테고리 목록 가져오기
            List<String> categories = dictionaryService.getAllCategories();
            
            request.setAttribute("term", term);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/pages/dictionary_edit.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 처리
        String path = request.getPathInfo();
        
        if (path.equals("/write")) {
            // 용어 등록 처리
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            String term = request.getParameter("term");
            String definition = request.getParameter("definition");
            String category = request.getParameter("category");
            
            if (term == null || term.trim().isEmpty() || 
                definition == null || definition.trim().isEmpty() || 
                category == null || category.trim().isEmpty()) {
                
                request.setAttribute("error", "모든 필드를 입력해주세요.");
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = dictionaryService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/dictionary_write.jsp").forward(request, response);
                return;
            }
            
            // 이미지 업로드 처리
            String imageUrl = null;
            
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/uploads/dictionary");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                String uuid = UUID.randomUUID().toString();
                imageUrl = "uploads/dictionary/" + uuid + "_" + originalFileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + originalFileName);
            }
            
            // 용어 DTO 생성
            DictionaryDTO termDTO = new DictionaryDTO();
            termDTO.setTerm(term);
            termDTO.setDefinition(definition);
            termDTO.setCategory(category);
            termDTO.setImageUrl(imageUrl);
            termDTO.setCreatedDate(new Date());
            
            // 용어 저장
            int termId = dictionaryService.insertTerm(termDTO);
            
            // 저장 성공 시 상세 페이지로 이동
            if (termId > 0) {
                response.sendRedirect(request.getContextPath() + "/dictionary/view?id=" + termId);
            } else {
                request.setAttribute("error", "용어 저장 중 오류가 발생했습니다.");
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = dictionaryService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/dictionary_write.jsp").forward(request, response);
            }
        } else if (path.equals("/edit")) {
            // 용어 수정 처리
            int termId = Integer.parseInt(request.getParameter("termId"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            // 용어 정보 가져오기
            DictionaryDTO termDTO = dictionaryService.getTermById(termId);
            
            String term = request.getParameter("term");
            String definition = request.getParameter("definition");
            String category = request.getParameter("category");
            
            if (term == null || term.trim().isEmpty() || 
                definition == null || definition.trim().isEmpty() || 
                category == null || category.trim().isEmpty()) {
                
                request.setAttribute("error", "모든 필드를 입력해주세요.");
                request.setAttribute("term", termDTO);
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = dictionaryService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/dictionary_edit.jsp").forward(request, response);
                return;
            }
            
            // 이미지 업로드 처리
            String imageUrl = termDTO.getImageUrl();
            
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                // 기존 이미지가 있으면 삭제
                if (imageUrl != null) {
                    File oldFile = new File(getServletContext().getRealPath("/" + imageUrl));
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                
                String uploadDir = getServletContext().getRealPath("/uploads/dictionary");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                String uuid = UUID.randomUUID().toString();
                imageUrl = "uploads/dictionary/" + uuid + "_" + originalFileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + originalFileName);
            }
            
            // 용어 수정
            termDTO.setTerm(term);
            termDTO.setDefinition(definition);
            termDTO.setCategory(category);
            termDTO.setImageUrl(imageUrl);
            termDTO.setModifiedDate(new Date());
            
            boolean success = dictionaryService.updateTerm(termDTO);
            
            // 수정 성공 시 상세 페이지로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/dictionary/view?id=" + termId);
            } else {
                request.setAttribute("error", "용어 수정 중 오류가 발생했습니다.");
                request.setAttribute("term", termDTO);
                
                // 카테고리 목록 다시 가져오기
                List<String> categories = dictionaryService.getAllCategories();
                request.setAttribute("categories", categories);
                
                request.getRequestDispatcher("/pages/dictionary_edit.jsp").forward(request, response);
            }
        } else if (path.equals("/delete")) {
            // 용어 삭제 처리
            int termId = Integer.parseInt(request.getParameter("id"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            // 용어 정보 가져오기
            DictionaryDTO termDTO = dictionaryService.getTermById(termId);
            
            // 이미지가 있으면 삭제
            if (termDTO.getImageUrl() != null) {
                File file = new File(getServletContext().getRealPath("/" + termDTO.getImageUrl()));
                if (file.exists()) {
                    file.delete();
                }
            }
            
            // 용어 삭제
            boolean success = dictionaryService.deleteTerm(termId);
            
            // 삭제 후 목록으로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/dictionary");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "용어 삭제 중 오류가 발생했습니다.");
            }
        }
    }
}