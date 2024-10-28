package com.example.springbootawsweb.config.auth;

import com.example.springbootawsweb.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    // 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단
    // ex) @GetMapping()
    //     public String profile(@LoginUser SessionUser user) {}
    // 이런 식으로 @LoginUser가 적용되었는지 + 파라미터 타입이 SessionUser인지 확인
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
    }

    // 파라미터에 전달할 객체를 생성, 세션에서 객체를 가져옴
    // 컨트롤러 메서드에서 @LoginUser SessionUser user와 같은 파라미터가 있을 때 세션에서 객체를 꺼내와서 파라미터에 주입
    @Override
    public Object resolveArgument(
        MethodParameter parameter, // supportsParameter가 ture인 경우에 실행
        ModelAndViewContainer mavContainer, // 컨트롤러 메서드에서 모델에 데이터를 추가하거나 뷰 관련 설정할 때 사용
        NativeWebRequest webRequest, // 웹 요청과 관력된 다양한 정보에 접근 가능
        WebDataBinderFactory binderFactory // 데이터 바인딩을 위한 객체 생성에 사용됨.
    ) throws Exception {
        return httpSession.getAttribute("user");
    }
}
