package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kirini.dto.CommentDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 댓글 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class CommentDAO {
    
    /**
     * 새로운 댓글 등록
     * @param comment 댓글 정보
     * @return 등록 성공 여부
     */
    public boolean insertComment(CommentDTO comment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO comments (board_id, user_id, user_nickname, content, created_date, parent_comment_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, comment.getBoardId());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setString(3, comment.getUserNickname());
            pstmt.setString(4, comment.getContent());
            pstmt.setTimestamp(5, new java.sql.Timestamp(comment.getCreatedDate().getTime()));
            pstmt.setInt(6, comment.getParentCommentId());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    comment.setCommentId(rs.getInt(1));
                    success = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return success;
    }
    
    /**
     * 댓글 수정
     * @param comment 수정할 댓글 정보
     * @return 수정 성공 여부
     */
    public boolean updateComment(CommentDTO comment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE comments SET content = ?, modified_date = ? WHERE comment_id = ? AND is_deleted = FALSE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comment.getContent());
            pstmt.setTimestamp(2, new java.sql.Timestamp(comment.getModifiedDate().getTime()));
            pstmt.setInt(3, comment.getCommentId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 댓글 삭제 (실제 삭제가 아닌 deleted 플래그 설정)
     * @param commentId 삭제할 댓글 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteComment(int commentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE comments SET is_deleted = TRUE WHERE comment_id = ? OR parent_comment_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, commentId); // 대댓글도 함께 삭제
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 특정 게시글의 댓글 목록 조회
     * @param boardId 게시글 ID
     * @return 댓글 목록
     */
    public List<CommentDTO> getCommentsByBoardId(int boardId) {
        List<CommentDTO> commentList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM comments WHERE board_id = ? AND is_deleted = FALSE " +
                         "ORDER BY IF(parent_comment_id = 0, comment_id, parent_comment_id), created_date";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CommentDTO comment = new CommentDTO();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setBoardId(rs.getInt("board_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUserNickname(rs.getString("user_nickname"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedDate(rs.getTimestamp("created_date"));
                comment.setModifiedDate(rs.getTimestamp("modified_date"));
                comment.setParentCommentId(rs.getInt("parent_comment_id"));
                comment.setDeleted(rs.getBoolean("is_deleted"));
                
                commentList.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return commentList;
    }
    
    /**
     * ID로 댓글 조회
     * @param commentId 댓글 ID
     * @return 댓글 정보
     */
    public CommentDTO getCommentById(int commentId) {
        CommentDTO comment = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM comments WHERE comment_id = ? AND is_deleted = FALSE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, commentId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                comment = new CommentDTO();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setBoardId(rs.getInt("board_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUserNickname(rs.getString("user_nickname"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedDate(rs.getTimestamp("created_date"));
                comment.setModifiedDate(rs.getTimestamp("modified_date"));
                comment.setParentCommentId(rs.getInt("parent_comment_id"));
                comment.setDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return comment;
    }
    
    /**
     * 사용자가 작성한 댓글 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (1부터 시작)
     * @return 댓글 목록
     */
    public List<CommentDTO> getCommentsByUserId(int userId, int page) {
        List<CommentDTO> commentList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT c.*, b.title AS board_title " +
                         "FROM comments c " +
                         "JOIN boards b ON c.board_id = b.board_id " +
                         "WHERE c.user_id = ? AND c.is_deleted = FALSE " +
                         "ORDER BY c.created_date DESC LIMIT 10 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * 10); // 10개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CommentDTO comment = new CommentDTO();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setBoardId(rs.getInt("board_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUserNickname(rs.getString("user_nickname"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedDate(rs.getTimestamp("created_date"));
                comment.setModifiedDate(rs.getTimestamp("modified_date"));
                comment.setParentCommentId(rs.getInt("parent_comment_id"));
                comment.setDeleted(rs.getBoolean("is_deleted"));
                comment.setBoardTitle(rs.getString("board_title")); // 게시글 제목은 추가 정보로 포함
                
                commentList.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return commentList;
    }
    
    /**
     * 사용자가 작성한 전체 댓글 수 조회
     * @param userId 사용자 ID
     * @return 댓글 수
     */
    public int getTotalUserCommentCount(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM comments WHERE user_id = ? AND is_deleted = FALSE";
            
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