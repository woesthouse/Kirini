package com.kirini.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.kirini.dto.BoardDTO;
import com.kirini.dto.CommentDTO;
import com.kirini.dto.UserDTO;
import com.kirini.service.BoardService;
import com.kirini.service.CommentService;
import com.kirini.util.FileUploadUtil;

/**
 * 게시판 관련 요청을 처리하는 컨트롤러
 * 게시글 목록 조회, 작성, 수정, 삭제 등 담당
 */
public class BoardController extends HttpServlet implements Controller {
    private static final long serialVersionUID = 1L;
    private BoardService boardService;
    private CommentService commentService;
    
    public BoardController() {
        this.boardService = new BoardService();
        this.commentService = new CommentService();
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        if (boardService == null) {
            boardService = new BoardService();
        }
        if (commentService == null) {
            commentService = new CommentService();
        }
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            // 게시판 메인 페이지 (게시글 목록)
            String boardType = request.getParameter("type");
            if (boardType == null || boardType.isEmpty()) {
                boardType = "free"; // 기본값은 자유게시판
            }
            
            int page = 1;
            try {
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                }
            } catch (NumberFormatException e) {
                // 페이지 파라미터가 숫자가 아닌 경우 기본값 유지
                page = 1;
            }
            
            // 검색 기능
            String searchType = request.getParameter("searchType");
            String keyword = request.getParameter("keyword");
            
            List<BoardDTO> boardList = null;
            int totalCount = 0;
            
            if (searchType != null && keyword != null && !keyword.isEmpty()) {
                // 검색 조건이 있는 경우
                boardList = boardService.searchBoards(boardType, searchType, keyword, page);
                totalCount = boardService.getTotalSearchCount(boardType, searchType, keyword);
            } else {
                // 검색 조건이 없는 경우
                boardList = boardService.getBoardsByType(boardType, page);
                totalCount = boardService.getTotalCount(boardType);
            }
            
            int totalPages = (int) Math.ceil(totalCount / 10.0); // 페이지당 10개 게시글
            
            request.setAttribute("boardList", boardList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("boardType", boardType);
            request.setAttribute("searchType", searchType);
            request.setAttribute("keyword", keyword);
            
            request.getRequestDispatcher("/pages/board.jsp").forward(request, response);
        } else if (path.equals("/view")) {
            // 게시글 상세 조회
            int boardId = Integer.parseInt(request.getParameter("id"));
            
            // 조회수 증가
            boardService.increaseViewCount(boardId);
            
            // 게시글 정보 가져오기
            BoardDTO board = boardService.getBoardById(boardId);
            
            // 댓글 목록 가져오기
            List<CommentDTO> commentList = commentService.getCommentsByBoardId(boardId);
            
            request.setAttribute("board", board);
            request.setAttribute("commentList", commentList);
            request.getRequestDispatcher("/pages/board_view.jsp").forward(request, response);
        } else if (path.equals("/write")) {
            // 게시글 작성 페이지
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            String boardType = request.getParameter("type");
            if (boardType == null || boardType.isEmpty()) {
                boardType = "free"; // 기본값은 자유게시판
            }
            
            request.setAttribute("boardType", boardType);
            request.getRequestDispatcher("/pages/board_write.jsp").forward(request, response);
        } else if (path.equals("/edit")) {
            // 게시글 수정 페이지
            int boardId = Integer.parseInt(request.getParameter("id"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 게시글 정보 가져오기
            BoardDTO board = boardService.getBoardById(boardId);
            
            // 작성자 체크
            if (board.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
                return;
            }
            
            request.setAttribute("board", board);
            request.getRequestDispatcher("/pages/board_edit.jsp").forward(request, response);
        }
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 처리
        String path = request.getPathInfo();
        
        if (path.equals("/write")) {
            // 게시글 작성 처리
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            String boardType = request.getParameter("boardType");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            
            if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                request.setAttribute("error", "제목과 내용을 모두 입력해주세요.");
                request.setAttribute("boardType", boardType);
                request.getRequestDispatcher("/pages/board_write.jsp").forward(request, response);
                return;
            }
            
            // 파일 업로드 처리
            String fileName = null;
            String filePath = null;
            
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String uploadDir = getServletContext().getRealPath("/uploads");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                fileName = originalFileName;
                String uuid = UUID.randomUUID().toString();
                filePath = "uploads/" + uuid + "_" + fileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + fileName);
            }
            
            // 게시글 DTO 생성
            BoardDTO board = new BoardDTO();
            board.setTitle(title);
            board.setContent(content);
            board.setUserId(user.getUserId());
            board.setUserNickname(user.getNickname());
            board.setBoardType(boardType);
            board.setCreatedDate(new Date());
            board.setFileName(fileName);
            board.setFilePath(filePath);
            
            // 게시글 저장
            int boardId = boardService.insertBoard(board);
            
            // 저장 성공 시 상세 페이지로 이동
            if (boardId > 0) {
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
            } else {
                request.setAttribute("error", "게시글 저장 중 오류가 발생했습니다.");
                request.setAttribute("boardType", boardType);
                request.getRequestDispatcher("/pages/board_write.jsp").forward(request, response);
            }
        } else if (path.equals("/edit")) {
            // 게시글 수정 처리
            int boardId = Integer.parseInt(request.getParameter("boardId"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 게시글 정보 가져오기
            BoardDTO board = boardService.getBoardById(boardId);
            
            // 작성자 체크
            if (board.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
                return;
            }
            
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            
            if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                request.setAttribute("error", "제목과 내용을 모두 입력해주세요.");
                request.setAttribute("board", board);
                request.getRequestDispatcher("/pages/board_edit.jsp").forward(request, response);
                return;
            }
            
            // 파일 업로드 처리
            String fileName = board.getFileName();
            String filePath = board.getFilePath();
            
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                // 기존 파일이 있으면 삭제
                if (filePath != null) {
                    File oldFile = new File(getServletContext().getRealPath("/" + filePath));
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }
                
                String uploadDir = getServletContext().getRealPath("/uploads");
                
                // 디렉토리가 없으면 생성
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                // 파일명 중복 방지를 위한 UUID 사용
                String originalFileName = FileUploadUtil.getFileName(filePart);
                fileName = originalFileName;
                String uuid = UUID.randomUUID().toString();
                filePath = "uploads/" + uuid + "_" + fileName;
                
                // 파일 저장
                filePart.write(uploadDir + File.separator + uuid + "_" + fileName);
            }
            
            // 게시글 수정
            board.setTitle(title);
            board.setContent(content);
            board.setModifiedDate(new Date());
            board.setFileName(fileName);
            board.setFilePath(filePath);
            
            boolean success = boardService.updateBoard(board);
            
            // 수정 성공 시 상세 페이지로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
            } else {
                request.setAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
                request.setAttribute("board", board);
                request.getRequestDispatcher("/pages/board_edit.jsp").forward(request, response);
            }
        } else if (path.equals("/delete")) {
            // 게시글 삭제 처리
            int boardId = Integer.parseInt(request.getParameter("id"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 게시글 정보 가져오기
            BoardDTO board = boardService.getBoardById(boardId);
            
            // 작성자 체크
            if (board.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "삭제 권한이 없습니다.");
                return;
            }
            
            // 첨부 파일 삭제
            if (board.getFilePath() != null) {
                File file = new File(getServletContext().getRealPath("/" + board.getFilePath()));
                if (file.exists()) {
                    file.delete();
                }
            }
            
            // 게시글 삭제
            boolean success = boardService.deleteBoard(boardId);
            
            // 삭제 후 목록으로 이동
            if (success) {
                response.sendRedirect(request.getContextPath() + "/board?type=" + board.getBoardType());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "게시글 삭제 중 오류가 발생했습니다.");
            }
        } else if (path.equals("/comment/write")) {
            // 댓글 작성 처리
            int boardId = Integer.parseInt(request.getParameter("boardId"));
            String content = request.getParameter("content");
            int parentCommentId = 0;
            
            try {
                String parentIdStr = request.getParameter("parentCommentId");
                if (parentIdStr != null && !parentIdStr.isEmpty()) {
                    parentCommentId = Integer.parseInt(parentIdStr);
                }
            } catch (NumberFormatException e) {
                parentCommentId = 0;
            }
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            if (content == null || content.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId + "&error=empty_comment");
                return;
            }
            
            // 댓글 DTO 생성
            CommentDTO comment = new CommentDTO();
            comment.setBoardId(boardId);
            comment.setUserId(user.getUserId());
            comment.setUserNickname(user.getNickname());
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setParentCommentId(parentCommentId);
            
            // 댓글 저장
            boolean success = commentService.insertComment(comment);
            
            // 저장 후 게시글 상세 페이지로 이동
            if (success) {
                // 게시글 댓글 수 증가
                boardService.increaseCommentCount(boardId);
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
            } else {
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId + "&error=comment_fail");
            }
        } else if (path.equals("/comment/delete")) {
            // 댓글 삭제 처리
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            int boardId = Integer.parseInt(request.getParameter("boardId"));
            
            // 로그인 체크
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            // 댓글 정보 가져오기
            CommentDTO comment = commentService.getCommentById(commentId);
            
            // 작성자 체크
            if (comment.getUserId() != user.getUserId() && !"ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "삭제 권한이 없습니다.");
                return;
            }
            
            // 댓글 삭제
            boolean success = commentService.deleteComment(commentId);
            
            // 삭제 후 게시글 상세 페이지로 이동
            if (success) {
                // 게시글 댓글 수 감소
                boardService.decreaseCommentCount(boardId);
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId);
            } else {
                response.sendRedirect(request.getContextPath() + "/board/view?id=" + boardId + "&error=delete_fail");
            }
        }
    }
}