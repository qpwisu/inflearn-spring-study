package hello.core.member;

import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository ;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }


//    private final MemberRepository memberRepository;
//
//    @Autowired // ac.getBean(MemberRepository.class)
//    public MemberServiceImpl(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//
//    @Override
//    public void join(Member member) {
//        memberRepository.save(member);
//    }
//
//    @Override
//    public Member findMember(Long memberId) {
//        return memberRepository.findById(memberId);
//    }
//
//    // 테스트 용도
//    public MemberRepository getMemberRepository() {
//        return memberRepository;
//    }

}
