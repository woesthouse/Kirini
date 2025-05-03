package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kirini.dto.DictionaryDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 키보드 용어사전 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class DictionaryDAO {
    
    /**
     * 새로운 용어 등록
     * @param dictionary 용어사전 정보
     * @return 등록된 용어 ID (실패 시 0 반환)
     */
    public int insertDictionary(DictionaryDTO dictionary) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int dictionaryId = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO dictionary (term, definition, category, user_id, created_date, image_url) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, dictionary.getTerm());
            pstmt.setString(2, dictionary.getDefinition());
            pstmt.setString(3, dictionary.getCategory());
            pstmt.setInt(4, dictionary.getUserId());
            pstmt.setTimestamp(5, new java.sql.Timestamp(dictionary.getCreatedDate().getTime()));
            pstmt.setString(6, dictionary.getImageUrl());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    dictionaryId = rs.getInt(1);
                    dictionary.setDictionaryId(dictionaryId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionaryId;
    }
    
    /**
     * 용어 정보 수정
     * @param dictionary 수정할 용어사전 정보
     * @return 수정 성공 여부
     */
    public boolean updateDictionary(DictionaryDTO dictionary) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE dictionary SET term = ?, definition = ?, category = ?, modified_date = ?, image_url = ? " +
                         "WHERE dictionary_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dictionary.getTerm());
            pstmt.setString(2, dictionary.getDefinition());
            pstmt.setString(3, dictionary.getCategory());
            pstmt.setTimestamp(4, new java.sql.Timestamp(dictionary.getModifiedDate().getTime()));
            pstmt.setString(5, dictionary.getImageUrl());
            pstmt.setInt(6, dictionary.getDictionaryId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 용어 삭제
     * @param dictionaryId 삭제할 용어 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteDictionary(int dictionaryId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "DELETE FROM dictionary WHERE dictionary_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, dictionaryId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * ID로 용어 조회
     * @param dictionaryId 용어사전 ID
     * @return 용어사전 정보
     */
    public DictionaryDTO getDictionaryById(int dictionaryId) {
        DictionaryDTO dictionary = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT d.*, u.nickname AS user_nickname " +
                         "FROM dictionary d " +
                         "LEFT JOIN users u ON d.user_id = u.user_id " +
                         "WHERE d.dictionary_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, dictionaryId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dictionary = new DictionaryDTO();
                dictionary.setDictionaryId(rs.getInt("dictionary_id"));
                dictionary.setTerm(rs.getString("term"));
                dictionary.setDefinition(rs.getString("definition"));
                dictionary.setCategory(rs.getString("category"));
                dictionary.setViewCount(rs.getInt("view_count"));
                dictionary.setUserId(rs.getInt("user_id"));
                dictionary.setUserNickname(rs.getString("user_nickname"));
                dictionary.setCreatedDate(rs.getTimestamp("created_date"));
                dictionary.setModifiedDate(rs.getTimestamp("modified_date"));
                dictionary.setImageUrl(rs.getString("image_url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionary;
    }
    
    /**
     * 용어 이름으로 용어 조회
     * @param term 용어 이름
     * @return 용어사전 정보
     */
    public DictionaryDTO getDictionaryByTerm(String term) {
        DictionaryDTO dictionary = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT d.*, u.nickname AS user_nickname " +
                         "FROM dictionary d " +
                         "LEFT JOIN users u ON d.user_id = u.user_id " +
                         "WHERE d.term = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, term);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dictionary = new DictionaryDTO();
                dictionary.setDictionaryId(rs.getInt("dictionary_id"));
                dictionary.setTerm(rs.getString("term"));
                dictionary.setDefinition(rs.getString("definition"));
                dictionary.setCategory(rs.getString("category"));
                dictionary.setViewCount(rs.getInt("view_count"));
                dictionary.setUserId(rs.getInt("user_id"));
                dictionary.setUserNickname(rs.getString("user_nickname"));
                dictionary.setCreatedDate(rs.getTimestamp("created_date"));
                dictionary.setModifiedDate(rs.getTimestamp("modified_date"));
                dictionary.setImageUrl(rs.getString("image_url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionary;
    }
    
    /**
     * 모든 용어 목록 조회 (페이징)
     * @param page 페이지 번호 (1부터 시작)
     * @return 용어사전 목록
     */
    public List<DictionaryDTO> getAllDictionary(int page) {
        List<DictionaryDTO> dictionaryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT d.*, u.nickname AS user_nickname " +
                         "FROM dictionary d " +
                         "LEFT JOIN users u ON d.user_id = u.user_id " +
                         "ORDER BY d.term ASC LIMIT 20 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * 20); // 20개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DictionaryDTO dictionary = new DictionaryDTO();
                dictionary.setDictionaryId(rs.getInt("dictionary_id"));
                dictionary.setTerm(rs.getString("term"));
                dictionary.setDefinition(rs.getString("definition"));
                dictionary.setCategory(rs.getString("category"));
                dictionary.setViewCount(rs.getInt("view_count"));
                dictionary.setUserId(rs.getInt("user_id"));
                dictionary.setUserNickname(rs.getString("user_nickname"));
                dictionary.setCreatedDate(rs.getTimestamp("created_date"));
                dictionary.setModifiedDate(rs.getTimestamp("modified_date"));
                dictionary.setImageUrl(rs.getString("image_url"));
                
                dictionaryList.add(dictionary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionaryList;
    }
    
    /**
     * 카테고리별 용어 목록 조회
     * @param category 카테고리
     * @param page 페이지 번호 (1부터 시작)
     * @return 용어사전 목록
     */
    public List<DictionaryDTO> getDictionaryByCategory(String category, int page) {
        List<DictionaryDTO> dictionaryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT d.*, u.nickname AS user_nickname " +
                         "FROM dictionary d " +
                         "LEFT JOIN users u ON d.user_id = u.user_id " +
                         "WHERE d.category = ? " +
                         "ORDER BY d.term ASC LIMIT 20 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            pstmt.setInt(2, (page - 1) * 20); // 20개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DictionaryDTO dictionary = new DictionaryDTO();
                dictionary.setDictionaryId(rs.getInt("dictionary_id"));
                dictionary.setTerm(rs.getString("term"));
                dictionary.setDefinition(rs.getString("definition"));
                dictionary.setCategory(rs.getString("category"));
                dictionary.setViewCount(rs.getInt("view_count"));
                dictionary.setUserId(rs.getInt("user_id"));
                dictionary.setUserNickname(rs.getString("user_nickname"));
                dictionary.setCreatedDate(rs.getTimestamp("created_date"));
                dictionary.setModifiedDate(rs.getTimestamp("modified_date"));
                dictionary.setImageUrl(rs.getString("image_url"));
                
                dictionaryList.add(dictionary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionaryList;
    }
    
    /**
     * 검색 조건에 맞는 용어 목록 조회
     * @param keyword 검색 키워드
     * @param category 카테고리 (null이면 전체 카테고리 검색)
     * @param page 페이지 번호 (1부터 시작)
     * @return 검색 결과 용어사전 목록
     */
    public List<DictionaryDTO> searchDictionary(String keyword, String category, int page) {
        List<DictionaryDTO> dictionaryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT d.*, u.nickname AS user_nickname ");
            sqlBuilder.append("FROM dictionary d ");
            sqlBuilder.append("LEFT JOIN users u ON d.user_id = u.user_id ");
            sqlBuilder.append("WHERE (d.term LIKE ? OR d.definition LIKE ?) ");
            
            if (category != null && !category.isEmpty()) {
                sqlBuilder.append("AND d.category = ? ");
            }
            
            sqlBuilder.append("ORDER BY d.term ASC LIMIT 20 OFFSET ?");
            String sql = sqlBuilder.toString();
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            
            if (category != null && !category.isEmpty()) {
                pstmt.setString(3, category);
                pstmt.setInt(4, (page - 1) * 20); // 20개씩 페이징
            } else {
                pstmt.setInt(3, (page - 1) * 20); // 20개씩 페이징
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DictionaryDTO dictionary = new DictionaryDTO();
                dictionary.setDictionaryId(rs.getInt("dictionary_id"));
                dictionary.setTerm(rs.getString("term"));
                dictionary.setDefinition(rs.getString("definition"));
                dictionary.setCategory(rs.getString("category"));
                dictionary.setViewCount(rs.getInt("view_count"));
                dictionary.setUserId(rs.getInt("user_id"));
                dictionary.setUserNickname(rs.getString("user_nickname"));
                dictionary.setCreatedDate(rs.getTimestamp("created_date"));
                dictionary.setModifiedDate(rs.getTimestamp("modified_date"));
                dictionary.setImageUrl(rs.getString("image_url"));
                
                dictionaryList.add(dictionary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return dictionaryList;
    }
    
    /**
     * 전체 용어 수 조회
     * @return 전체 용어 수
     */
    public int getTotalCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM dictionary";
            
            pstmt = conn.prepareStatement(sql);
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
     * 카테고리별 용어 수 조회
     * @param category 카테고리
     * @return 카테고리별 용어 수
     */
    public int getTotalCountByCategory(String category) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM dictionary WHERE category = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            
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
     * 검색 결과의 전체 용어 수 조회
     * @param keyword 검색 키워드
     * @param category 카테고리 (null이면 전체 카테고리 검색)
     * @return 검색 결과 용어 수
     */
    public int getTotalSearchCount(String keyword, String category) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT COUNT(*) FROM dictionary WHERE (term LIKE ? OR definition LIKE ?) ");
            
            if (category != null && !category.isEmpty()) {
                sqlBuilder.append("AND category = ?");
            }
            
            String sql = sqlBuilder.toString();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            
            if (category != null && !category.isEmpty()) {
                pstmt.setString(3, category);
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
     * 모든 카테고리 목록 조회
     * @return 카테고리 목록
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT DISTINCT category FROM dictionary ORDER BY category";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return categories;
    }
    
    /**
     * 조회수 증가
     * @param dictionaryId 용어사전 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseViewCount(int dictionaryId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE dictionary SET view_count = view_count + 1 WHERE dictionary_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, dictionaryId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
}