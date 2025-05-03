package com.kirini.dto;

import java.util.Date;

/**
 * 사용자 정보를 담는 DTO 클래스
 */
public class UserDTO {
    private int userId;           // 사용자 ID
    private String email;         // 이메일
    private String password;      // 비밀번호 (보안 상 DB에서 조회 후 DTO에는 저장하지 않는 것이 좋음)
    private String nickname;      // 닉네임
    private String role;          // 권한 (ADMIN, USER)
    private Date registrationDate; // 가입일
    private Date lastLoginDate;   // 마지막 로그인 일시
    private boolean active;       // 활성화 여부
    
    // 기본 생성자
    public UserDTO() {
    }
    
    // Getter와 Setter 메서드
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public Date getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "UserDTO [userId=" + userId + ", email=" + email + ", nickname=" + nickname + ", role=" + role
                + ", registrationDate=" + registrationDate + ", lastLoginDate=" + lastLoginDate + ", active=" + active
                + "]";
    }
}