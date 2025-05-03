package com.kirini.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kirini.dto.UserDTO;
import com.kirini.util.DBConnectionUtil;

/**
 * 사용자 정보에 관한 데이터베이스 접근을 담당하는 DAO
 */
public class UserDAO {
    
    /**
     * 회원가입 처리
     * @param user 등록할 사용자 정보
     * @return 가입 성공 여부
     */
    public boolean registerUser(UserDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "INSERT INTO users (email, password, nickname, role, registration_date, active) " +
                         "VALUES (?, ?, ?, ?, NOW(), TRUE)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword()); // 실제로는 암호화된 비밀번호를 저장해야 함
            pstmt.setString(3, user.getNickname());
            pstmt.setString(4, user.getRole() != null ? user.getRole() : "USER");
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 정보
     */
    public UserDTO getUserByEmail(String email) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND active = TRUE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                user.setLastLoginDate(rs.getTimestamp("last_login_date"));
                user.setActive(rs.getBoolean("active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return user;
    }
    
    /**
     * 로그인 처리
     * @param email 이메일
     * @param password 비밀번호
     * @return 로그인 성공 시 사용자 정보, 실패 시 null
     */
    public UserDTO login(String email, String password) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            // 실제로는 비밀번호를 암호화하여 비교해야 함
            String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND active = TRUE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // 실제로는 암호화된 비밀번호로 비교해야 함
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                user.setLastLoginDate(rs.getTimestamp("last_login_date"));
                user.setActive(rs.getBoolean("active"));
                
                // 마지막 로그인 시간 업데이트
                updateLastLoginDate(user.getUserId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return user;
    }
    
    /**
     * 마지막 로그인 시간 업데이트
     * @param userId 사용자 ID
     * @return 업데이트 성공 여부
     */
    public boolean updateLastLoginDate(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE users SET last_login_date = NOW() WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 사용자 정보 업데이트
     * @param user 수정할 사용자 정보
     * @return 업데이트 성공 여부
     */
    public boolean updateUser(UserDTO user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE users SET nickname = ? WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getNickname());
            pstmt.setInt(2, user.getUserId());
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 비밀번호 변경
     * @param userId 사용자 ID
     * @param newPassword 새 비밀번호
     * @return 변경 성공 여부
     */
    public boolean changePassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE users SET password = ? WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword); // 실제로는 암호화된 비밀번호를 저장해야 함
            pstmt.setInt(2, userId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * 사용자 비활성화 (탈퇴)
     * @param userId 사용자 ID
     * @return 비활성화 성공 여부
     */
    public boolean deactivateUser(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE users SET active = FALSE WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
    
    /**
     * ID로 사용자 조회
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    public UserDTO getUserById(int userId) {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM users WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                user.setLastLoginDate(rs.getTimestamp("last_login_date"));
                user.setActive(rs.getBoolean("active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return user;
    }
    
    /**
     * 닉네임 중복 검사
     * @param nickname 검사할 닉네임
     * @return 중복 여부 (중복이면 true)
     */
    public boolean isNicknameExists(String nickname) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE nickname = ? AND active = TRUE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nickname);
            
            rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return exists;
    }
    
    /**
     * 이메일 중복 검사
     * @param email 검사할 이메일
     * @return 중복 여부 (중복이면 true)
     */
    public boolean isEmailExists(String email) {
        boolean exists = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND active = TRUE";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return exists;
    }
    
    /**
     * 전체 사용자 목록 조회 (관리자 기능)
     * @param page 페이지 번호 (1부터 시작)
     * @param pageSize 페이지당 항목 수
     * @return 사용자 목록
     */
    public List<UserDTO> getAllUsers(int page, int pageSize) {
        List<UserDTO> userList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT * FROM users ORDER BY registration_date DESC LIMIT ? OFFSET ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pageSize);
            pstmt.setInt(2, (page - 1) * pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setEmail(rs.getString("email"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                user.setLastLoginDate(rs.getTimestamp("last_login_date"));
                user.setActive(rs.getBoolean("active"));
                
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(rs, pstmt, conn);
        }
        
        return userList;
    }
    
    /**
     * 전체 사용자 수 조회
     * @return 전체 사용자 수
     */
    public int getTotalUserCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM users";
            
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
     * 관리자 권한 부여 또는 해제
     * @param userId 사용자 ID
     * @param isAdmin 관리자 여부
     * @return 업데이트 성공 여부
     */
    public boolean setAdminRole(int userId, boolean isAdmin) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        try {
            conn = DBConnectionUtil.getConnection();
            String sql = "UPDATE users SET role = ? WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isAdmin ? "ADMIN" : "USER");
            pstmt.setInt(2, userId);
            
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnectionUtil.close(null, pstmt, conn);
        }
        
        return result > 0;
    }
}