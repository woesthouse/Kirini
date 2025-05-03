package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kirini.dto.BoardFileDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 게시판 첨부 파일 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class BoardFileDAO {
    
    /**
     * 새로운 첨부 파일 정보 등록
     * @param boardFile 첨부 파일 정보
     * @return 등록된 파일 ID (실패 시 0 반환)
     */
    public int insertBoardFile(BoardFileDTO boardFile) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int fileId = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO board_files (board_id, original_file_name, saved_file_path, file_size, " +
                         "file_type, upload_date, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, boardFile.getBoardId());
            pstmt.setString(2, boardFile.getOriginalFileName());
            pstmt.setString(3, boardFile.getSavedFilePath());
            pstmt.setLong(4, boardFile.getFileSize());
            pstmt.setString(5, boardFile.getFileType());
            pstmt.setTimestamp(6, new java.sql.Timestamp(boardFile.getUploadDate().getTime()));
            pstmt.setBoolean(7, boardFile.isDeleted());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    fileId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return fileId;
    }
    
    /**
     * ID로 첨부 파일 정보 조회
     * @param fileId 파일 ID
     * @return 첨부 파일 정보
     */
    public BoardFileDTO getBoardFileById(int fileId) {
        BoardFileDTO boardFile = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM board_files WHERE file_id = ? AND is_deleted = false";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boardFile = new BoardFileDTO();
                boardFile.setFileId(rs.getInt("file_id"));
                boardFile.setBoardId(rs.getInt("board_id"));
                boardFile.setOriginalFileName(rs.getString("original_file_name"));
                boardFile.setSavedFilePath(rs.getString("saved_file_path"));
                boardFile.setFileSize(rs.getLong("file_size"));
                boardFile.setFileType(rs.getString("file_type"));
                boardFile.setUploadDate(rs.getTimestamp("upload_date"));
                boardFile.setDeleted(rs.getBoolean("is_deleted"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardFile;
    }
    
    /**
     * 게시글 ID로 첨부 파일 목록 조회
     * @param boardId 게시글 ID
     * @return 첨부 파일 목록
     */
    public List<BoardFileDTO> getBoardFilesByBoardId(int boardId) {
        List<BoardFileDTO> boardFiles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM board_files WHERE board_id = ? AND is_deleted = false ORDER BY file_id";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BoardFileDTO boardFile = new BoardFileDTO();
                boardFile.setFileId(rs.getInt("file_id"));
                boardFile.setBoardId(rs.getInt("board_id"));
                boardFile.setOriginalFileName(rs.getString("original_file_name"));
                boardFile.setSavedFilePath(rs.getString("saved_file_path"));
                boardFile.setFileSize(rs.getLong("file_size"));
                boardFile.setFileType(rs.getString("file_type"));
                boardFile.setUploadDate(rs.getTimestamp("upload_date"));
                boardFile.setDeleted(rs.getBoolean("is_deleted"));
                
                boardFiles.add(boardFile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return boardFiles;
    }
    
    /**
     * 첨부 파일 삭제 (실제 삭제가 아닌 deleted 플래그 설정)
     * @param fileId 삭제할 파일 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteBoardFile(int fileId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE board_files SET is_deleted = true WHERE file_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 게시글의 모든 첨부 파일 삭제 (실제 삭제가 아닌 deleted 플래그 설정)
     * @param boardId 게시글 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteAllBoardFiles(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE board_files SET is_deleted = true WHERE board_id = ?";
            
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
     * 이미지 파일 목록 조회 (게시글 ID 기준)
     * @param boardId 게시글 ID
     * @return 이미지 파일 목록
     */
    public List<BoardFileDTO> getImageFilesByBoardId(int boardId) {
        List<BoardFileDTO> imageFiles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM board_files WHERE board_id = ? AND is_deleted = false " +
                         "AND (file_type LIKE 'image/%' OR original_file_name LIKE '%.jpg' OR " +
                         "original_file_name LIKE '%.jpeg' OR original_file_name LIKE '%.png' OR " +
                         "original_file_name LIKE '%.gif' OR original_file_name LIKE '%.bmp' OR " +
                         "original_file_name LIKE '%.webp') ORDER BY file_id";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BoardFileDTO boardFile = new BoardFileDTO();
                boardFile.setFileId(rs.getInt("file_id"));
                boardFile.setBoardId(rs.getInt("board_id"));
                boardFile.setOriginalFileName(rs.getString("original_file_name"));
                boardFile.setSavedFilePath(rs.getString("saved_file_path"));
                boardFile.setFileSize(rs.getLong("file_size"));
                boardFile.setFileType(rs.getString("file_type"));
                boardFile.setUploadDate(rs.getTimestamp("upload_date"));
                boardFile.setDeleted(rs.getBoolean("is_deleted"));
                
                imageFiles.add(boardFile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return imageFiles;
    }
    
    /**
     * 첨부 파일 수 조회 (게시글 ID 기준)
     * @param boardId 게시글 ID
     * @return 첨부 파일 수
     */
    public int countBoardFiles(int boardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM board_files WHERE board_id = ? AND is_deleted = false";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, boardId);
            
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