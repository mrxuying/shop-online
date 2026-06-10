package com.shop.online.infrastructure.security;

import cn.hutool.http.HtmlUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * XSS 防御过滤器 — 过滤请求参数中的 HTML 标签
 */
@Component
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
    }

    /**
     * 包装 HttpServletRequest，对参数值做 HTML 转义
     */
    private static class XssRequestWrapper extends HttpServletRequestWrapper {

        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            String[] filtered = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                filtered[i] = HtmlUtil.filter(values[i]);
            }
            return filtered;
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            if (value == null) {
                return null;
            }
            return HtmlUtil.filter(value);
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            if (value == null) {
                return null;
            }
            return HtmlUtil.filter(value);
        }
    }
}
