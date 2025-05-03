package com.kirini.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * URL 패턴과 핸들러(컨트롤러) 객체를 매핑하는 클래스
 * 싱글톤 패턴으로 구현되어 애플리케이션 전체에서 하나의 인스턴스만 사용
 */
public class HandlerMapping {
    // 싱글톤 인스턴스
    private static HandlerMapping instance = new HandlerMapping();
    
    // URL 패턴과 핸들러 객체를 매핑하는 맵 (Thread-safe)
    private Map<String, Object> mappings = new ConcurrentHashMap<>();
    
    // 생성자를 private으로 선언하여 외부에서 인스턴스 생성 방지
    private HandlerMapping() {}
    
    /**
     * 싱글톤 인스턴스 반환
     * @return HandlerMapping 인스턴스
     */
    public static HandlerMapping getInstance() {
        return instance;
    }
    
    /**
     * URL 패턴과 핸들러 매핑 정보 설정
     * @param mappings URL과 핸들러 객체의 매핑 정보
     */
    public void setMappings(Map<String, Object> mappings) {
        this.mappings.clear();
        this.mappings.putAll(mappings);
    }
    
    /**
     * 주어진 URL에 맞는 핸들러(컨트롤러) 객체 반환
     * 정확히 일치하는 URL이 없을 경우 와일드카드 패턴(/* 형식)을 확인
     * @param url 요청 URL
     * @return 매핑된 핸들러 객체, 없으면 null 반환
     */
    public Object getHandler(String url) {
        // 1. 정확한 URL 매칭 시도
        if (mappings.containsKey(url)) {
            return mappings.get(url);
        }
        
        // 2. 정확한 매칭이 없으면 와일드카드 매칭 시도
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            String baseUrl = url.substring(0, lastSlashIndex);
            String wildcardUrl = baseUrl + "/*";
            
            if (mappings.containsKey(wildcardUrl)) {
                return mappings.get(wildcardUrl);
            }
        }
        
        // 3. 매칭되는 핸들러가 없을 경우
        return null;
    }
    
    /**
     * 현재 등록된 모든 URL 매핑 정보 반환
     * @return URL과 핸들러 객체의 매핑 정보
     */
    public Map<String, Object> getMappings() {
        return new ConcurrentHashMap<>(mappings);
    }
}