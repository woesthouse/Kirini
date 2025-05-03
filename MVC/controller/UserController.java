package com.kirini.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kirini.dto.UserDTO;
import com.kirini.service.UserService;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 * 회원가입, 로그인, 로그아웃, 프로필 조회/수정 등 담당
 */
@WebServlet("/user/*")
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path == null || path.equals("/")) {
            // 마이페이지 (프로필) 표시
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            request.setAttribute("user", user);
            request.getRequestDispatcher("/pages/mypage.jsp").forward(request, response);
        } else if (path.equals("/login")) {
            // 로그인 페이지 표시
            request.getRequestDispatcher("/pages/login.html").forward(request, response);
        } else if (path.equals("/logout")) {
            // 로그아웃 처리
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            // 자동 로그인 쿠키 삭제
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("rememberMe")) {
                        cookie.setValue("");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        break;
                    }
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/");
        } else if (path.equals("/signup")) {
            // 회원가입 페이지 표시
            request.getRequestDispatcher("/pages/signup.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        if (path.equals("/login")) {
            // 로그인 처리
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String rememberMe = request.getParameter("remember-me");
            
            UserDTO user = userService.login(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // 로그인 유지 체크 시 쿠키 생성
                if (rememberMe != null && rememberMe.equals("on")) {
                    Cookie cookie = new Cookie("rememberMe", email);
                    cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
                
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                request.setAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
                request.getRequestDispatcher("/pages/login.html").forward(request, response);
            }
        } else if (path.equals("/signup")) {
            // 회원가입 처리
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String nickname = request.getParameter("nickname");
            
            // 이메일 중복 확인
            if (userService.isEmailDuplicated(email)) {
                request.setAttribute("error", "이미 사용 중인 이메일입니다.");
                request.getRequestDispatcher("/pages/signup.jsp").forward(request, response);
                return;
            }
            
            // 닉네임 중복 확인
            if (userService.isNicknameDuplicated(nickname)) {
                request.setAttribute("error", "이미 사용 중인 닉네임입니다.");
                request.getRequestDispatcher("/pages/signup.jsp").forward(request, response);
                return;
            }
            
            boolean success = userService.register(email, password, nickname);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/user/login");
            } else {
                request.setAttribute("error", "회원가입 중 오류가 발생했습니다.");
                request.getRequestDispatcher("/pages/signup.jsp").forward(request, response);
            }
        } else if (path.equals("/update")) {
            // 회원 정보 수정 처리
            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("user");
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/user/login");
                return;
            }
            
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String nickname = request.getParameter("nickname");
            
            boolean success = userService.updateUser(user.getUserId(), currentPassword, newPassword, nickname);
            if (success) {
                // 세션 업데이트
                UserDTO updatedUser = userService.getUserById(user.getUserId());
                session.setAttribute("user", updatedUser);
                
                request.setAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
            } else {
                request.setAttribute("error", "회원 정보 수정 중 오류가 발생했습니다.");
            }
            
            request.getRequestDispatcher("/pages/mypage.jsp").forward(request, response);
        }
    }
}