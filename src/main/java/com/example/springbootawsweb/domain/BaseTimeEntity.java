package com.example.springbootawsweb.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
// jpa entity 클래스들이 BaseTimeEntity를 상속할 경우 필드들도 칼럼으로 인식하도록 함
@MappedSuperclass // 테이블과 관계가 없이 공통 메핑 정보를 모으는 역할 -> 조회와 검색 불가능, 직접 생성해서 사용할 경우가 없으므로 추상 클래스 권장
@EntityListeners(AuditingEntityListener.class) // auditing 기능(엔티티 생성, 수정 시간 자동으로 관리)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
