package com.kirini.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 모든 컨트롤러가 구현해야 하는 인터페이스
 * HandlerMapping을 통해 관리되는 모든 컨트롤러는 이 인터페이스를 구현해야 함
 */
public interface Controller {
    /**
     * HTTP GET 요청 처리
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @throws ServletException 서블릿 처리 중 발생한 예외
     * @throws IOException 입출력 처리 중 발생한 예외
     */
    void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
    
    /**
     * HTTP POST 요청 처리
     * 
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @throws ServletException 서블릿 처리 중 발생한 예외
     * @throws IOException 입출력 처리 중 발생한 예외
     */
    void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException;
}