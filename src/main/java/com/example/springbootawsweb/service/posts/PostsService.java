package com.example.springbootawsweb.service.posts;

import com.example.springbootawsweb.domain.posts.Posts;
import com.example.springbootawsweb.domain.posts.PostsRepository;
import com.example.springbootawsweb.web.dto.PostsListResponseDto;
import com.example.springbootawsweb.web.dto.PostsResponseDto;
import com.example.springbootawsweb.web.dto.PostsSaveRequestDto;
import com.example.springbootawsweb.web.dto.PostsUpdateRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // 생성자 대신 Bean 주입, final이 선언된 모든 필드를 인자값으로 하는 생성자를 생성
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    // update 기능에서 db에 쿼리 날리는 부분이 없다. JPA의 영속성 컨텍스트 때문에 가능하다.
    // 영속성 컨텍스트란 엔티티를 영구 저장하는 환경이다. 엔티티가 영속성 컨텍스트에 포함되어 있냐 아니냐로 갈린다.
    // jpa의 엔티티 매니저가 활성화된 상태로 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 테이터는 영속성 컨텍스트가 유지된 상태이다.
    // 이 상태에서 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영한다.
    // entity 객체의 값만 변경하면 별도로 update 쿼리를 날릴 필요가 없는 의미이다. 이를 더티 체킹이라고 한다.
    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        return new PostsResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream()
            .map(PostsListResponseDto::new)
            .toList();
    }

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        postsRepository.delete(posts);
    }
}
