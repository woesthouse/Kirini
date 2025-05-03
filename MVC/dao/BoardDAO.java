package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kirini.dto.BoardDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 게시글 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class BoardDAO {
    
    /**
     * 게시글 등록
     * @param board 게시글 정보
     * @return 등록된 게시글 ID (실패 시 0 반환)
     */
    public int insertBoard(BoardDTO board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int boardId = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO boards (title, content, user_id, user_nickname, board_type, created_date, file_name, file_path) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, board.getTitle());
            pstmt.setString(2, board.getContent());
            pstmt.setInt(3, board.getUserId());
            pstmt.setString(4, board.getUserNickname());
            pstmt.setString(5, board.getBoardType());
            pstmt.setTimestamp(6, new java.sql.Timestamp(board.getCreatedDate().getTime()));
            pstmt.setString(7, board.getFileName());
            pstmt.setString(8, board.getFilePath());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    boardId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardId;
    }
    
    /**
     * 게시글 수정
     * @param board 수정할 게시글 정보
     * @return 수정 성공 여부
     */
    public boolean updateBoard(BoardDTO board) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET title = ?, content = ?, modified_date = ?, file_name = ?, file_path = ? " +
                         "WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, board.getTitle());
            pstmt.setString(2, board.getContent());
            pstmt.setTimestamp(3, new java.sql.Timestamp(board.getModifiedDate().getTime()));
            pstmt.setString(4, board.getFileName());
            pstmt.setString(5, board.getFilePath());
            pstmt.setInt(6, board.getBoardId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글 삭제 (실제 삭제가 아닌 deleted 플래그 설정)
     * @param boardId 삭제할 게시글 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteBoard(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET is_deleted = TRUE WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * ID로 게시글 조회
     * @param boardId 게시글 ID
     * @return 게시글 정보
     */
    public BoardDTO getBoardById(int boardId) {
        BoardDTO board = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM boards WHERE board_id = ? AND is_deleted = FALSE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                board = new BoardDTO();
                board.setBoardId(rs.getInt("board_id"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setUserId(rs.getInt("user_id"));
                board.setUserNickname(rs.getString("user_nickname"));
                board.setBoardType(rs.getString("board_type"));
                board.setViewCount(rs.getInt("view_count"));
                board.setLikeCount(rs.getInt("like_count"));
                board.setCommentCount(rs.getInt("comment_count"));
                board.setCreatedDate(rs.getTimestamp("created_date"));
                board.setModifiedDate(rs.getTimestamp("modified_date"));
                board.setDeleted(rs.getBoolean("is_deleted"));
                board.setFileName(rs.getString("file_name"));
                board.setFilePath(rs.getString("file_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return board;
    }
    
    /**
     * 게시판 타입별 게시글 목록 조회
     * @param boardType 게시판 타입 (news, free, anonymous)
     * @param page 페이지 번호 (1부터 시작)
     * @return 게시글 목록
     */
    public List<BoardDTO> getBoardsByType(String boardType, int page) {
        List<BoardDTO> boardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM boards WHERE board_type = ? AND is_deleted = FALSE " +
                         "ORDER BY created_date DESC LIMIT 10 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, boardType);
            pstmt.setInt(2, (page - 1) * 10); // 10개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BoardDTO board = new BoardDTO();
                board.setBoardId(rs.getInt("board_id"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setUserId(rs.getInt("user_id"));
                board.setUserNickname(rs.getString("user_nickname"));
                board.setBoardType(rs.getString("board_type"));
                board.setViewCount(rs.getInt("view_count"));
                board.setLikeCount(rs.getInt("like_count"));
                board.setCommentCount(rs.getInt("comment_count"));
                board.setCreatedDate(rs.getTimestamp("created_date"));
                board.setModifiedDate(rs.getTimestamp("modified_date"));
                board.setDeleted(rs.getBoolean("is_deleted"));
                board.setFileName(rs.getString("file_name"));
                board.setFilePath(rs.getString("file_path"));
                
                boardList.add(board);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardList;
    }
    
    /**
     * 게시판 타입별 전체 게시글 수 조회
     * @param boardType 게시판 타입 (news, free, anonymous)
     * @return 게시글 수
     */
    public int getTotalCount(String boardType) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM boards WHERE board_type = ? AND is_deleted = FALSE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, boardType);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return count;
    }
    
    /**
     * 게시글 검색
     * @param boardType 게시판 타입 (news, free, anonymous)
     * @param searchType 검색 타입 (title, content, user)
     * @param keyword 검색어
     * @param page 페이지 번호 (1부터 시작)
     * @return 검색 결과 게시글 목록
     */
    public List<BoardDTO> searchBoards(String boardType, String searchType, String keyword, int page) {
        List<BoardDTO> boardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT * FROM boards WHERE board_type = ? AND is_deleted = FALSE");
            
            if ("title".equals(searchType)) {
                sqlBuilder.append(" AND title LIKE ?");
            } else if ("content".equals(searchType)) {
                sqlBuilder.append(" AND content LIKE ?");
            } else if ("user".equals(searchType)) {
                sqlBuilder.append(" AND user_nickname LIKE ?");
            } else {
                sqlBuilder.append(" AND (title LIKE ? OR content LIKE ?)");
            }
            
            sqlBuilder.append(" ORDER BY created_date DESC LIMIT 10 OFFSET ?");
            String sql = sqlBuilder.toString();
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, boardType);
            
            String searchKeyword = "%" + keyword + "%";
            if ("title".equals(searchType) || "content".equals(searchType) || "user".equals(searchType)) {
                pstmt.setString(2, searchKeyword);
                pstmt.setInt(3, (page - 1) * 10); // 10개씩 페이징
            } else {
                pstmt.setString(2, searchKeyword);
                pstmt.setString(3, searchKeyword);
                pstmt.setInt(4, (page - 1) * 10); // 10개씩 페이징
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BoardDTO board = new BoardDTO();
                board.setBoardId(rs.getInt("board_id"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setUserId(rs.getInt("user_id"));
                board.setUserNickname(rs.getString("user_nickname"));
                board.setBoardType(rs.getString("board_type"));
                board.setViewCount(rs.getInt("view_count"));
                board.setLikeCount(rs.getInt("like_count"));
                board.setCommentCount(rs.getInt("comment_count"));
                board.setCreatedDate(rs.getTimestamp("created_date"));
                board.setModifiedDate(rs.getTimestamp("modified_date"));
                board.setDeleted(rs.getBoolean("is_deleted"));
                board.setFileName(rs.getString("file_name"));
                board.setFilePath(rs.getString("file_path"));
                
                boardList.add(board);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardList;
    }
    
    /**
     * 검색 결과 전체 게시글 수 조회
     * @param boardType 게시판 타입 (news, free, anonymous)
     * @param searchType 검색 타입 (title, content, user)
     * @param keyword 검색어
     * @return 검색 결과 게시글 수
     */
    public int getTotalSearchCount(String boardType, String searchType, String keyword) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT COUNT(*) FROM boards WHERE board_type = ? AND is_deleted = FALSE");
            
            if ("title".equals(searchType)) {
                sqlBuilder.append(" AND title LIKE ?");
            } else if ("content".equals(searchType)) {
                sqlBuilder.append(" AND content LIKE ?");
            } else if ("user".equals(searchType)) {
                sqlBuilder.append(" AND user_nickname LIKE ?");
            } else {
                sqlBuilder.append(" AND (title LIKE ? OR content LIKE ?)");
            }
            
            String sql = sqlBuilder.toString();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, boardType);
            
            String searchKeyword = "%" + keyword + "%";
            if ("title".equals(searchType) || "content".equals(searchType) || "user".equals(searchType)) {
                pstmt.setString(2, searchKeyword);
            } else {
                pstmt.setString(2, searchKeyword);
                pstmt.setString(3, searchKeyword);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return count;
    }
    
    /**
     * 게시글 조회수 증가
     * @param boardId 게시글 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseViewCount(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET view_count = view_count + 1 WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글 좋아요 수 증가
     * @param boardId 게시글 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseLikeCount(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET like_count = like_count + 1 WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글 좋아요 수 감소
     * @param boardId 게시글 ID
     * @return 업데이트 성공 여부
     */
    public boolean decreaseLikeCount(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET like_count = GREATEST(like_count - 1, 0) WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글 댓글 수 증가
     * @param boardId 게시글 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseCommentCount(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET comment_count = comment_count + 1 WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글 댓글 수 감소
     * @param boardId 게시글 ID
     * @return 업데이트 성공 여부
     */
    public boolean decreaseCommentCount(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE boards SET comment_count = GREATEST(comment_count - 1, 0) WHERE board_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 사용자가 작성한 게시글 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (1부터 시작)
     * @return 게시글 목록
     */
    public List<BoardDTO> getBoardsByUserId(int userId, int page) {
        List<BoardDTO> boardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM boards WHERE user_id = ? AND is_deleted = FALSE " +
                         "ORDER BY created_date DESC LIMIT 10 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * 10); // 10개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BoardDTO board = new BoardDTO();
                board.setBoardId(rs.getInt("board_id"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setUserId(rs.getInt("user_id"));
                board.setUserNickname(rs.getString("user_nickname"));
                board.setBoardType(rs.getString("board_type"));
                board.setViewCount(rs.getInt("view_count"));
                board.setLikeCount(rs.getInt("like_count"));
                board.setCommentCount(rs.getInt("comment_count"));
                board.setCreatedDate(rs.getTimestamp("created_date"));
                board.setModifiedDate(rs.getTimestamp("modified_date"));
                board.setDeleted(rs.getBoolean("is_deleted"));
                board.setFileName(rs.getString("file_name"));
                board.setFilePath(rs.getString("file_path"));
                
                boardList.add(board);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardList;
    }
    
    /**
     * 사용자가 작성한 전체 게시글 수 조회
     * @param userId 사용자 ID
     * @return 게시글 수
     */
    public int getTotalUserBoardCount(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM boards WHERE user_id = ? AND is_deleted = FALSE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return count;
    }
}