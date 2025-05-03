package com.kirini.controller;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kirini.util.HandlerMapping;

/**
 * 모든 요청을 받아서 적절한 컨트롤러로 요청을 위임하는 프론트 컨트롤러
 * HandlerMapping을 통해 요청 URL에 해당하는 컨트롤러를 찾아 처리
 */
@WebServlet("*.do")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,     // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 요청 URL 경로 추출
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String command = requestURI.substring(contextPath.length());
        
        System.out.println("DispatcherServlet - 요청 URL: " + command);
        
        try {
            // URL에 맞는 컨트롤러 찾기
            HandlerMapping handlerMapping = HandlerMapping.getInstance();
            Object controller = handlerMapping.getHandler(command);
            
            if (controller == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청한 페이지를 찾을 수 없습니다.");
                return;
            }
            
            // HTTP 메서드에 따라 컨트롤러의 적절한 메서드 호출
            String method = request.getMethod();
            if ("GET".equalsIgnoreCase(method)) {
                invokeMethod(controller, "doGet", request, response);
            } else if ("POST".equalsIgnoreCase(method)) {
                invokeMethod(controller, "doPost", request, response);
            } else {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.");
            }
            
        } catch (Exception e) {
            System.err.println("DispatcherServlet 오류: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }
    
    /**
     * 리플렉션을 사용하여 컨트롤러의 메서드 호출
     * @param controller 컨트롤러 객체
     * @param methodName 호출할 메서드 이름 (doGet 또는 doPost)
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @throws Exception 메서드 호출 중 발생한 예외
     */
    private void invokeMethod(Object controller, String methodName, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        try {
            Method method = controller.getClass().getMethod(methodName, 
                    HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(controller, request, response);
        } catch (NoSuchMethodException e) {
            System.err.println("컨트롤러에 " + methodName + " 메서드가 없습니다: " + controller.getClass().getName());
            throw e;
        } catch (Exception e) {
            System.err.println("메서드 호출 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }
}