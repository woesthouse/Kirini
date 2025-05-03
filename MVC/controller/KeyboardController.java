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

import com.kirini.dto.KeyboardDTO;
import com.kirini.dto.UserDTO;
import com.kirini.service.KeyboardService;
import com.kirini.util.FileUploadUtil;

/**
 * 키보드 정보 관련 요청을 처리하는 컨트롤러
 * 키보드 목록 조회, 상세 조회, 등록, 수정, 삭제 등 담당
 */
@WebServlet("/keyboard/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class KeyboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private KeyboardService keyboardService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        keyboardService = new KeyboardService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            // 키보드 정보 메인 페이지 (키보드 목록)
            int page = 1;
            try {
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
            
            // 필터링 및 검색 기능
            String brand = request.getParameter("brand");
            String switchType = request.getParameter("switch");
            String size = request.getParameter("size");
            String connection = request.getParameter("connection");
            String keyword = request.getParameter("keyword");
            String sortBy = request.getParameter("sort");
            
            // 가격 범위 필터링
            int minPrice = 0;
            int maxPrice = Integer.MAX_VALUE;
            try {
                String minPriceStr = request.getParameter("minPrice");
                if (minPriceStr != null && !minPriceStr.isEmpty()) {
                    minPrice = Integer.parseInt(minPriceStr);
                }
                
                String maxPriceStr = request.getParameter("maxPrice");
                if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                    maxPrice = Integer.parseInt(maxPriceStr);
                }
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우 기본값 유지
            }
            
            List<KeyboardDTO> keyboardList = keyboardService.searchKeyboards(
                brand, switchType, size, connection, minPrice, maxPrice, keyword, sortBy, page);
            
            int totalCount = keyboardService.getTotalSearchCount(
                brand, switchType, size, connection, minPrice, maxPrice, keyword);
            
            int totalPages = (int) Math.ceil(totalCount / 12.0); // 페이지당 12개 키보드
            
            // 브랜드, 스위치 타입, 사이즈, 연결 방식 목록 조회
            List<String> brandList = keyboardService.getAllBrands();
            List<String> switchTypeList = keyboardService.getAllSwitchTypes();
            List<String> sizeList = keyboardService.getAllSizes();
            List<String> connectionList = keyboardService.getAllConnectionTypes();
            
            request.setAttribute("keyboardList", keyboardList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("brand", brand);
            request.setAttribute("switchType", switchType);
            request.setAttribute("size", size);
            request.setAttribute("connection", connection);
            request.setAttribute("minPrice", minPrice == 0 ? null : minPrice);
            request.setAttribute("maxPrice", maxPrice == Integer.MAX_VALUE ? null : maxPrice);
            request.setAttribute("keyword", keyword);
            request.setAttribute("sortBy", sortBy);
            
            request.setAttribute("brandList", brandList);
            request.setAttribute("switchTypeList", switchTypeList);
            request.setAttribute("sizeList", sizeList);
            request.setAttribute("connectionList", connectionList);
            
            request.getRequestDispatcher("/pages/keyboard_info.jsp").forward(request, response);
        } else if (path.equals("/view")) {
            // 키보드 상세 정보 조회
            int keyboardId = Integer.parseInt(request.getParameter("id"));
            
            KeyboardDTO keyboard = keyboardService.getKeyboardById(keyboardId);
            
            request.setAttribute("keyboard", keyboard);
            request.getRequestDispatcher("/pages/keyboard_view.jsp").forward(request, response);
        } else if (path.equals("/write")) {
            // 키보드 등록 페이지
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            // 브랜드, 스위치 타입, 사이즈, 연결 방식 목록 조회
            List<String> brandList = keyboardService.getAllBrands();
            List<String> switchTypeList = keyboardService.getAllSwitchTypes();
            List<String> sizeList = keyboardService.getAllSizes();
            List<String> connectionList = keyboardService.getAllConnectionTypes();
            
            request.setAttribute("brandList", brandList);
            request.setAttribute("switchTypeList", switchTypeList);
            request.setAttribute("sizeList", sizeList);
            request.setAttribute("connectionList", connectionList);
            
            request.getRequestDispatcher("/pages/keyboard_write.jsp").forward(request, response);
        } else if (path.equals("/edit")) {
            // 키보드 수정 페이지
            int keyboardId = Integer.parseInt(request.getParameter("id"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            KeyboardDTO keyboard = keyboardService.getKeyboardById(keyboardId);
            
            // 브랜드, 스위치 타입, 사이즈, 연결 방식 목록 조회
            List<String> brandList = keyboardService.getAllBrands();
            List<String> switchTypeList = keyboardService.getAllSwitchTypes();
            List<String> sizeList = keyboardService.getAllSizes();
            List<String> connectionList = keyboardService.getAllConnectionTypes();
            
            request.setAttribute("keyboard", keyboard);
            request.setAttribute("brandList", brandList);
            request.setAttribute("switchTypeList", switchTypeList);
            request.setAttribute("sizeList", sizeList);
            request.setAttribute("connectionList", connectionList);
            
            request.getRequestDispatcher("/pages/keyboard_edit.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 처리
        String path = request.getPathInfo();
        
        if (path.equals("/write")) {
            // 키보드 등록 처리
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            String name = request.getParameter("name");
            String brand = request.getParameter("brand");
            String switchType = request.getParameter("switchType");
            String keycapMaterial = request.getParameter("keycapMaterial");
            String size = request.getParameter("size");
            String connection = request.getParameter("connection");
            String layout = request.getParameter("layout");
            String description = request.getParameter("description");
            
            boolean isHotSwap = "on".equals(request.getParameter("isHotSwap"));
            
            int price = 0;
            try {
                price = Integer.parseInt(request.getParameter("price"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "가격은 숫자로 입력해주세요.");
                doGet(request, response);
                return;
            }
            
            if (name == null || name.trim().isEmpty() || 
                brand == null || brand.trim().isEmpty() || 
                switchType == null || switchType.trim().isEmpty() || 
                size == null || size.trim().isEmpty()) {
                
                request.setAttribute("error", "필수 입력 항목을 모두 입력해주세요.");
                doGet(request, response);
                return;
            }
            
            // 이미지 업로드 처리
            String imageUrl = null;
            
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/uploads/keyboard");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                String uuid = UUID.randomUUID().toString();
                imageUrl = "uploads/keyboard/" + uuid + "_" + originalFileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + originalFileName);
            }
            
            // 키보드 DTO 생성
            KeyboardDTO keyboard = new KeyboardDTO();
            keyboard.setName(name);
            keyboard.setBrand(brand);
            keyboard.setSwitchType(switchType);
            keyboard.setKeycapMaterial(keycapMaterial);
            keyboard.setSize(size);
            keyboard.setConnection(connection);
            keyboard.setHotSwap(isHotSwap);
            keyboard.setLayout(layout);
            keyboard.setPrice(price);
            keyboard.setDescription(description);
            keyboard.setImageUrl(imageUrl);
            keyboard.setRegisteredDate(new Date());
            
            // 키보드 저장
            int keyboardId = keyboardService.insertKeyboard(keyboard);
            
            // 저장 성공 시 상세 페이지로 이동
            if (keyboardId > 0) {
                response.sendRedirect(request.getContextPath() + "/keyboard/view?id=" + keyboardId);
            } else {
                request.setAttribute("error", "키보드 정보 저장 중 오류가 발생했습니다.");
                doGet(request, response);
            }
        } else if (path.equals("/edit")) {
            // 키보드 수정 처리
            int keyboardId = Integer.parseInt(request.getParameter("keyboardId"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            KeyboardDTO keyboard = keyboardService.getKeyboardById(keyboardId);
            
            String name = request.getParameter("name");
            String brand = request.getParameter("brand");
            String switchType = request.getParameter("switchType");
            String keycapMaterial = request.getParameter("keycapMaterial");
            String size = request.getParameter("size");
            String connection = request.getParameter("connection");
            String layout = request.getParameter("layout");
            String description = request.getParameter("description");
            
            boolean isHotSwap = "on".equals(request.getParameter("isHotSwap"));
            
            int price = 0;
            try {
                price = Integer.parseInt(request.getParameter("price"));
            } catch (NumberFormatException e) {
                request.setAttribute("error", "가격은 숫자로 입력해주세요.");
                request.setAttribute("keyboard", keyboard);
                doGet(request, response);
                return;
            }
            
            if (name == null || name.trim().isEmpty() || 
                brand == null || brand.trim().isEmpty() || 
                switchType == null || switchType.trim().isEmpty() || 
                size == null || size.trim().isEmpty()) {
                
                request.setAttribute("error", "필수 입력 항목을 모두 입력해주세요.");
                request.setAttribute("keyboard", keyboard);
                doGet(request, response);
                return;
            }
            
            // 이미지 업로드 처리
            String imageUrl = keyboard.getImageUrl();
            
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                // 기존 이미지가 있으면 삭제
                if (imageUrl != null) {
                    File oldFile = new File(getServletContext().getRealPath("/" + imageUrl));
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                
                String uploadDir = getServletContext().getRealPath("/uploads/keyboard");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                String uuid = UUID.randomUUID().toString();
                imageUrl = "uploads/keyboard/" + uuid + "_" + originalFileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + originalFileName);
            }
            
            // 키보드 수정
            keyboard.setName(name);
            keyboard.setBrand(brand);
            keyboard.setSwitchType(switchType);
            keyboard.setKeycapMaterial(keycapMaterial);
            keyboard.setSize(size);
            keyboard.setConnection(connection);
            keyboard.setHotSwap(isHotSwap);
            keyboard.setLayout(layout);
            keyboard.setPrice(price);
            keyboard.setDescription(description);
            keyboard.setImageUrl(imageUrl);
            
            boolean success = keyboardService.updateKeyboard(keyboard);
            
            // 수정 성공 시 상세 페이지로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/keyboard/view?id=" + keyboardId);
            } else {
                request.setAttribute("error", "키보드 정보 수정 중 오류가 발생했습니다.");
                request.setAttribute("keyboard", keyboard);
                doGet(request, response);
            }
        } else if (path.equals("/delete")) {
            // 키보드 삭제 처리
            int keyboardId = Integer.parseInt(request.getParameter("id"));
            
            // 관리자 권한 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null || !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
                return;
            }
            
            KeyboardDTO keyboard = keyboardService.getKeyboardById(keyboardId);
            
            // 이미지가 있으면 삭제
            if (keyboard.getImageUrl() != null) {
                File file = new File(getServletContext().getRealPath("/" + keyboard.getImageUrl()));
                if (file.exists()) {
                    file.delete();
                }
            }
            
            // 키보드 삭제
            boolean success = keyboardService.deleteKeyboard(keyboardId);
            
            // 삭제 후 목록으로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/keyboard");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "키보드 정보 삭제 중 오류가 발생했습니다.");
            }
        }
    }
}