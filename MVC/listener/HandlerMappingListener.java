package com.kirini.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.kirini.controller.BoardController;
import com.kirini.controller.DictionaryController;
import com.kirini.controller.FileDownloadServlet;
import com.kirini.controller.FileUploadServlet;
import com.kirini.controller.KeyboardController;
import com.kirini.controller.QnaController;
import com.kirini.controller.UserController;
import com.kirini.util.HandlerMapping;

/**
 * 웹 애플리케이션 시작 시 URL과 컨트롤러의 매핑 정보를 초기화하는 리스너
 */
@WebListener
public class HandlerMappingListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("HandlerMappingListener - 컨텍스트 초기화 시작");
        
        ServletContext context = event.getServletContext();
        HandlerMapping handlerMapping = HandlerMapping.getInstance();
        
        // URL과 컨트롤러 매핑 정보 설정
        Map<String, Object> mappings = new HashMap<>();
        
        // Board 관련 URL 매핑
        mappings.put("/board", new BoardController());
        mappings.put("/board/*", new BoardController());
        
        // Dictionary 관련 URL 매핑
        mappings.put("/dictionary", new DictionaryController());
        mappings.put("/dictionary/*", new DictionaryController());
        
        // QnA 관련 URL 매핑
        mappings.put("/qna", new QnaController());
        mappings.put("/qna/*", new QnaController());
        
        // Keyboard 관련 URL 매핑
        mappings.put("/keyboard", new KeyboardController());
        mappings.put("/keyboard/*", new KeyboardController());
        
        // 파일 업로드/다운로드 관련 URL 매핑
        mappings.put("/upload", new FileUploadServlet());
        mappings.put("/download", new FileDownloadServlet());
        
        // 사용자 관련 URL 매핑
        mappings.put("/user", new UserController());
        mappings.put("/user/*", new UserController());
        
        // 매핑 정보 설정
        handlerMapping.setMappings(mappings);
        
        // 컨텍스트에 핸들러매핑 객체 저장
        context.setAttribute("handlerMapping", handlerMapping);
        
        System.out.println("HandlerMappingListener - URL 매핑 정보 초기화 완료");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("HandlerMappingListener - 컨텍스트 종료");
    }
}