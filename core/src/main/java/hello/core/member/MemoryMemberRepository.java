package hello.core.member;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
// 메모리에 저장해서 테스트 하기 위함
@Component
public class MemoryMemberRepository implements MemberRepository{

    private static Map<Long, Member> store = new HashMap<>(); // 동시성 이슈 발생

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }
}