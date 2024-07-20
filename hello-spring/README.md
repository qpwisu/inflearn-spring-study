## 섹션[2]

- restapi @ResponseBody : 결과를 http body에 넣어서 전달
    - HttpMessageConverter가 결과가 문자 → string, 결과가 객체 → json 바꿔서 전달

---

## 섹션[3]

**파일 구조** 

- controller, domain, repository,service로 나눔
- repository에서  memberRepository는 인터페이스로 MemoryMemberRepository는 이를 구현한 구현체

**map, ArrayList 동시성 문제** 

- 실무에서는 hashmap는 여러스레드의 동시접근 문제가 발생할수 있어서 ConcurrentHashMap을 사용해야한다
    - ArrayList는 CopyOnWriteArrayList로
    
    ```idris
    private static Map<Long,Member> store = new HashMap<>();
    대신 
    private static Map<Long, Member> store = new ConcurrentHashMap<>();
    
    리스트 
    private static List<Member> members = new CopyOnWriteArrayList<>();
    
    ```
    

**테스트 Junit**

- 테스트하고자 하는 파일명 뒤에 + Test 붙혀서 만듬 (public 안붙혀도됨)
- **테스트 파일 생성 커맨드  :** 파일내 class명 스크롤 → (command+ shift + T)
- 테스트는 서로 의존성이 없어야함
- 테스트 메서드명은 한글로도 많이함
- @Test : 테스트 생성
- @BeforeEach : 메소드 시작전 실행 테스트가 서로 영향이 없도록 항상 새로운 객체를 생성하고, 의존관계도 새로 맺어준다.
- @AfterEach : 메소드가 끝날때 마다 한번씩 실행, 테스트 데이터 초기화시 유용함
- Assertions.asserEquals(a,b) : 둘이 같으면 통과 다르면 오류 발생
- Assertions.assertThat(a).istEqualTo(b) : 위에 같은 기능 많이 쓰임
- **given(주어진거), when(언제), then(결과)**을 주석으로 나눠서 테스트 코드 작성
- assertThrows를 통해 특정 예외가 발생하는지 테스트

```java
class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach // 생성자 주입 
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    public void 회원가입() throws Exception {
        // Given
        Member member = new Member();
        member.setName("hello");

        // When
        Long saveId = memberService.join(member);

        // Then
        Member findMember = memberRepository.findById(saveId).get();
        assertEquals(member.getName(), findMember.getName());
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // Given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // When
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2)); // 예외가 발생해야 한다.

        // Then
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}
```

**Service에서 respository 생성자 주입 방식(테스트 용의)**

```java
public class MemberService {
    private final MemberRepository memberRepository;

    // 생성자를 통해 MemberRepository를 주입받음
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
```

**service 개발** 

- Optional 다루기:
    - get() : 값 꺼내기
    - ifPresent : optional안에 값이 있으면 동작
    - orElse : 값이 있으면 반환 없으면 대체값을 반환 (대체값을 항상 계산)
    - orElseGet : 값이 있으면 반환 없으면 대체값을 반환 (optional이 비어있을때만 대체값 계산) [실무 많이 사용]
    
    ```idris
    
    Optional<Member> result = memberRepository.findByName(member.getName());
    
    String value = result.get();
    optional.ifPresent(value -> System.out.println("Value is present: " + value));
    String emptyValue = emptyOptional.orElse(getDefaultValue());
    String emptyValue = emptyOptional.orElseGet(() -> getDefaultValue());
    
    # 이렇게도 많이 사용 
    memberRepository.findByName(member.getName())
    		.ifPresent(m -> {
    	        throw new IllegalStateException("이미 존재하는 회원입니다.");
    	    });
    ```
    
- 로직 메서드로 변환 ⭐
    - 로직 스크롤 후 (컨트롤 + T) → extract method  아니면 (커맨드 + Option + M)

---

## 섹션[4]

- 생성자 주입(권장)
    - 생성자를 통해 의존관계를 주입
    - 한번만 호출되기 때문에 final로 관리 가능
    - 생성자 주입 사용 시, **생성자가 1개인 경우 @Autowired를 생략할 수 있다.**
    - 변경 가능성이 없는 의존 관계에 사용
    - @RequiredArgsConstructor를 붙히면 final,@notnull 이 붙은 필드를 인자로 받는 생성자를 자동으로 생성해줌
- 수정자(setter) 주입
    - 변경 가능성이 있는 의존 관계에 사용
    - 생성자 호출 이후 필드 변수에 변경이 일어날 수 있어 final 사용 불가능
- 필드 주입
    - autowired 만 붙이면 자동으로 의존성 주입
    - 코드가 간결하지만 외부접근이 불가능해 테스트가 어려움
    - DI프레임워크가 없으면 사용 할 수 없음, 사용 권장 하지 않음

```java
# 생성자 
@Service
public class StationConstructorService {
    private final StationRepository stationRepository;

    @Autowired
    public StationConstructorService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }
}

# setter 
@Service
public class StationSetterService {
    private StationRepository stationRepository;

    @Autowired
    public void setStationRepository(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }
}

# field 
@Service
public class StationFieldService {
    @Autowired
    private StationRepository stationRepository;

}
```

## 세션[6]

통합 테스트 

- @SpringBootTest
- @Transactional - 테스트가 끝나면 롤백해줌

JPA

- service에 항상  @Transactional 있어야됨
- 내가 해온 간단한 CRUD를 제공하는건 Spring Data Jpa이다
- 어려운 문제를 해결하기위 기본 JPA를 사용할 줄 알아야한다
- EntityManager를 통해 jpql 사용
- 기술
    - **Querydsl**: 타입 안전한 쿼리 작성, 자바 코드 기반.
    - **JPQL**: 객체 지향 쿼리 언어, 엔티티 객체를 대상으로 SQL 유사하게 작성.
    - **JPA**: ORM 표준 명세, 객체-관계 매핑 관리.
    - **Spring Data JPA**: JPA의 편리한 사용을 위해 CRUD 기능을 자동으로 제공.
    - **MyBatis**: SQL 매퍼 프레임워크, 직접 SQL 작성 및 XML 매핑.

## 세션[7]

**AOP : 공통 관심 사항(cross-cutting concern) vs 핵심 관심 사항(core concern) 분리**

- 회원가입, 회원 조회에 시간을 측정하는 기능은 핵심 관심 사항이 아니다.
- 시간을 측정하는 로직은 공통 관심 사항이다.
- 시간을 측정하는 로직과 핵심 비즈니스의 로직이 섞여서 유지보수가 어렵다.
- 시간을 측정하는 로직을 별도의 공통 로직으로 만들기 매우 어렵다.

```java
package hello.hellospring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component // 스프링 빈으로 등록
@Aspect // AOP 애스펙트로 사용
public class TimeTraceAop {

    @Around("execution(* hello.hellospring..*(..))") // hello.hellospring 패키지와 하위 패키지의 모든 메소드에 적용
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis(); // 메소드 시작 시간 기록
        System.out.println("START: " + joinPoint.toString()); // 시작 로그 출력
        try {
            return joinPoint.proceed(); // 실제 메소드 실행
        } finally {
            long finish = System.currentTimeMillis(); // 메소드 종료 시간 기록
            long timeMs = finish - start; // 실행 시간 계산
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms"); // 종료 로그 출력
        }
    }
}

```