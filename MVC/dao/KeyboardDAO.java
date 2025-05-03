package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kirini.dto.KeyboardDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 키보드 제품 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class KeyboardDAO {
    
    /**
     * 새로운 키보드 정보 등록
     * @param keyboard 키보드 정보
     * @return 등록된 키보드 ID (실패 시 0 반환)
     */
    public int insertKeyboard(KeyboardDTO keyboard) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int keyboardId = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO keyboards (name, brand, price, switch_type, layout, backlight, " +
                        "connectivity, keycap_material, case_material, description, user_id, " +
                        "created_date, image_url) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, keyboard.getName());
            pstmt.setString(2, keyboard.getBrand());
            pstmt.setInt(3, keyboard.getPrice());
            pstmt.setString(4, keyboard.getSwitchType());
            pstmt.setString(5, keyboard.getLayout());
            pstmt.setString(6, keyboard.getBacklight());
            pstmt.setString(7, keyboard.getConnectivity());
            pstmt.setString(8, keyboard.getKeycapMaterial());
            pstmt.setString(9, keyboard.getCaseMaterial());
            pstmt.setString(10, keyboard.getDescription());
            pstmt.setInt(11, keyboard.getUserId());
            pstmt.setTimestamp(12, new java.sql.Timestamp(keyboard.getCreatedDate().getTime()));
            pstmt.setString(13, keyboard.getImageUrl());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    keyboardId = rs.getInt(1);
                    keyboard.setKeyboardId(keyboardId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardId;
    }
    
    /**
     * 키보드 정보 수정
     * @param keyboard 수정할 키보드 정보
     * @return 수정 성공 여부
     */
    public boolean updateKeyboard(KeyboardDTO keyboard) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE keyboards SET name = ?, brand = ?, price = ?, switch_type = ?, layout = ?, " +
                        "backlight = ?, connectivity = ?, keycap_material = ?, case_material = ?, description = ?, " +
                        "modified_date = ?, image_url = ? " +
                        "WHERE keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, keyboard.getName());
            pstmt.setString(2, keyboard.getBrand());
            pstmt.setInt(3, keyboard.getPrice());
            pstmt.setString(4, keyboard.getSwitchType());
            pstmt.setString(5, keyboard.getLayout());
            pstmt.setString(6, keyboard.getBacklight());
            pstmt.setString(7, keyboard.getConnectivity());
            pstmt.setString(8, keyboard.getKeycapMaterial());
            pstmt.setString(9, keyboard.getCaseMaterial());
            pstmt.setString(10, keyboard.getDescription());
            pstmt.setTimestamp(11, new java.sql.Timestamp(keyboard.getModifiedDate().getTime()));
            pstmt.setString(12, keyboard.getImageUrl());
            pstmt.setInt(13, keyboard.getKeyboardId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 키보드 정보 삭제
     * @param keyboardId 삭제할 키보드 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteKeyboard(int keyboardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "DELETE FROM keyboards WHERE keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, keyboardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * ID로 키보드 정보 조회
     * @param keyboardId 키보드 ID
     * @return 키보드 정보
     */
    public KeyboardDTO getKeyboardById(int keyboardId) {
        KeyboardDTO keyboard = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, keyboardId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboard;
    }
    
    /**
     * 모든 키보드 정보 목록 조회 (페이징)
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> getAllKeyboards(int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "ORDER BY k.created_date DESC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 브랜드별 키보드 목록 조회
     * @param brand 브랜드명
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> getKeyboardsByBrand(String brand, int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.brand = ? " +
                        "ORDER BY k.created_date DESC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, brand);
            pstmt.setInt(2, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 스위치 타입별 키보드 목록 조회
     * @param switchType 스위치 타입
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> getKeyboardsBySwitchType(String switchType, int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.switch_type = ? " +
                        "ORDER BY k.created_date DESC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, switchType);
            pstmt.setInt(2, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 레이아웃별 키보드 목록 조회
     * @param layout 레이아웃 (full, tenkeyless, 60%, etc.)
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> getKeyboardsByLayout(String layout, int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.layout = ? " +
                        "ORDER BY k.created_date DESC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, layout);
            pstmt.setInt(2, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 가격 범위별 키보드 목록 조회
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> getKeyboardsByPriceRange(int minPrice, int maxPrice, int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.price BETWEEN ? AND ? " +
                        "ORDER BY k.price ASC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, minPrice);
            pstmt.setInt(2, maxPrice);
            pstmt.setInt(3, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 키보드 이름/브랜드 검색
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (1부터 시작)
     * @return 키보드 정보 목록
     */
    public List<KeyboardDTO> searchKeyboards(String keyword, int page) {
        List<KeyboardDTO> keyboardList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT k.*, u.nickname AS user_nickname FROM keyboards k " +
                        "LEFT JOIN users u ON k.user_id = u.user_id " +
                        "WHERE k.name LIKE ? OR k.brand LIKE ? OR k.description LIKE ? " +
                        "ORDER BY k.created_date DESC LIMIT 12 OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            pstmt.setInt(4, (page - 1) * 12); // 12개씩 페이징
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                KeyboardDTO keyboard = new KeyboardDTO();
                keyboard.setKeyboardId(rs.getInt("keyboard_id"));
                keyboard.setName(rs.getString("name"));
                keyboard.setBrand(rs.getString("brand"));
                keyboard.setPrice(rs.getInt("price"));
                keyboard.setSwitchType(rs.getString("switch_type"));
                keyboard.setLayout(rs.getString("layout"));
                keyboard.setBacklight(rs.getString("backlight"));
                keyboard.setConnectivity(rs.getString("connectivity"));
                keyboard.setKeycapMaterial(rs.getString("keycap_material"));
                keyboard.setCaseMaterial(rs.getString("case_material"));
                keyboard.setDescription(rs.getString("description"));
                keyboard.setViewCount(rs.getInt("view_count"));
                keyboard.setLikeCount(rs.getInt("like_count"));
                keyboard.setUserId(rs.getInt("user_id"));
                keyboard.setUserNickname(rs.getString("user_nickname"));
                keyboard.setCreatedDate(rs.getTimestamp("created_date"));
                keyboard.setModifiedDate(rs.getTimestamp("modified_date"));
                keyboard.setImageUrl(rs.getString("image_url"));
                
                keyboardList.add(keyboard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return keyboardList;
    }
    
    /**
     * 전체 키보드 수 조회
     * @return 전체 키보드 수
     */
    public int getTotalCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM keyboards";
            
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
     * 조건별 키보드 수 조회 (브랜드, 스위치타입, 레이아웃 등)
     * @param field 필드명 (brand, switch_type, layout)
     * @param value 값
     * @return 조건에 맞는 키보드 수
     */
    public int getCountByField(String field, String value) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM keyboards WHERE " + field + " = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            
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
     * 가격 범위별 키보드 수 조회
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 가격 범위 내 키보드 수
     */
    public int getCountByPriceRange(int minPrice, int maxPrice) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM keyboards WHERE price BETWEEN ? AND ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, minPrice);
            pstmt.setInt(2, maxPrice);
            
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
     * 검색 결과 키보드 수 조회
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 키보드 수
     */
    public int getSearchCount(String keyword) {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM keyboards WHERE name LIKE ? OR brand LIKE ? OR description LIKE ?";
            
            pstmt = conn.prepareStatement(sql);
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            pstmt.setString(3, searchKeyword);
            
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
     * 모든 브랜드 목록 조회
     * @return 브랜드 목록
     */
    public List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT DISTINCT brand FROM keyboards ORDER BY brand";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                brands.add(rs.getString("brand"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return brands;
    }
    
    /**
     * 모든 스위치 타입 목록 조회
     * @return 스위치 타입 목록
     */
    public List<String> getAllSwitchTypes() {
        List<String> switchTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT DISTINCT switch_type FROM keyboards ORDER BY switch_type";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                switchTypes.add(rs.getString("switch_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return switchTypes;
    }
    
    /**
     * 모든 레이아웃 목록 조회
     * @return 레이아웃 목록
     */
    public List<String> getAllLayouts() {
        List<String> layouts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT DISTINCT layout FROM keyboards ORDER BY layout";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                layouts.add(rs.getString("layout"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return layouts;
    }
    
    /**
     * 조회수 증가
     * @param keyboardId 키보드 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseViewCount(int keyboardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE keyboards SET view_count = view_count + 1 WHERE keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, keyboardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 좋아요 수 증가
     * @param keyboardId 키보드 ID
     * @return 업데이트 성공 여부
     */
    public boolean increaseLikeCount(int keyboardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE keyboards SET like_count = like_count + 1 WHERE keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, keyboardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 좋아요 수 감소
     * @param keyboardId 키보드 ID
     * @return 업데이트 성공 여부
     */
    public boolean decreaseLikeCount(int keyboardId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE keyboards SET like_count = GREATEST(like_count - 1, 0) WHERE keyboard_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, keyboardId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
}