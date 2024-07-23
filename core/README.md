## 세션[1] 객체 지향 설계와 스프링 (20240719)

---

스프링 부트 

- 스프링을 편리하게 사용할 수 있게 지원, 최근은 기본
- 외부 라이브러리 자동 구성(라이브러리 끼리 맞는 버전을 자동으로 관리해줌),웹서버() 내장,

객체지향 프로그래밍

- 객체들의 모임으로 각각의 객체는 메세지를 주고 받고 데이터를 처리할 수 있다
- 프로그램이 유연하고 변경이 용이해진다

**다형성** 

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/8dccd74f-01d9-4af5-98f4-ee20793c05b5/Untitled.png)

역할과 구현으로 구분하면 세상이 단순해지고, 유연해지며 변경도 편리해진다.
장점

- 클라이언트는 대상의 역할(인터페이스)만 알면 된다.
- 클라이언트는 구현 대상의 내부 구조를 몰라도 된다.
- 클라이언트는 구현 대상의 내부 구조가 변경되어도 영향을 받지 않는다.
- 클라이언트는 구현 대상 자체를 변경해도 영향을 받지 않는다.

### 자바의 다형성

- 역할 = 인터페이스
- 구현 = 인터페이스를 구현한 클래스, 구현 객체
- 역할과 구현을 명확히 분리

### 오버라이딩

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/1765a549-fd94-4c9b-8c69-f340840d1312/Untitled.png)

```java
public class MemberService {
//  private MemberRepository memberRepository = new MemoryMemberRepository();
private MemberRepository memberRepository = new JdbcMemberRepository();
}
```

- 멤버서비스가 멤버레퍼지토리를 의존
- MemberRepository 인터페이스에 JdbcMemberRepository,MemoryMemberRepository 중 하나를 선택해 넣을 수 있다(다형성)
    - 부모인 MemberRepository(interface) 자식을 받아드릴 수 있다.

### SOLID

- srp 단일 책임 원칙 :  한 클래스는 하나의 책임
- **ocp** 개방 폐쇄의 원칙 : 확장에는 열리게 변경에 닫히게
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/1765a549-fd94-4c9b-8c69-f340840d1312/Untitled.png)
    
    - memberservice에서 memorymr를 jdbcm레포로 구현 클래스를 변경해야하는데?? → ocp가 깨짐
        - 이를 해결하기 위해  필요한게  DI, 스프링(ioc) 컨테이너
- lsp 리스코프 치환
    - 프로그램의 객체는 프로그램의 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀
    수 있어야 한다
    - 다형성에서 하위 클래스는 인터페이스 규약을 다 지켜야 한다는 것, 다형성을 지원하기 위
    한 원칙, 인터페이스를 구현한 구현체는 믿고 사용하려면, 이 원칙이 필요하다.
- isp 인터페이스 분리
    - **특정 클라이언트를 위한 인터페이스 여러 개가 범용 인터페이스 하나보다 낫다**
    - 사용자 클라이언트 -> 운전자 클라이언트, 정비사 클라이언트로 분리
    - 분리하면 정비 인터페이스 자체가 변해도 운전자 클라이언트에 영향을 주지 않음
    인터페이스가 명확해지고, 대체 가능성이 높아진다
- dip 의존관계 역전
    - 프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” 의존성 주입은 이 원칙
    을 따르는 방법 중 하나다.
    - 쉽게 이야기해서 구현 클래스에 의존하지 말고, 인터페이스에 의존하라는 뜻
        - memberservice가 memberRepository만 바라보고 구현체는 몰라야한다
            - 위 사진에서 memberservice는 인터페이스 뿐만 아니라 JdbcMemberRepository(구현체)에도 의존하고 있다
            - 오로지 MemberRepository에만 의존하게 만들어야한다
    - 앞에서 이야기한 역할(Role)에 의존하게 해야 한다는 것과 같다. 객체 세상도 클라이언트
    가 인터페이스에 의존해야 유연하게 구현체를 변경할 수 있다! 구현체에 의존하게 되면 변
    경이 아주 어려워진다.

- **다향성만으로는 OCP DIP를 만족할 수 없다**
    - 구현객체가 변경될때 클라이언트 코드도 변경된다

**실무 고민**

- 하지만 인터페이스를 도입하면 추상화라는 비용이 발생한다.
- 기능을 확장할 가능성이 없다면, 구체 클래스를 직접 사용하고, 향후 꼭 필요할 때 리팩터
링해서 인터페이스를 도입하는 것도 방법이다

## 세션[2] 스프링 핵심 원리 이해1 - 예제 만들기 (20240719)

---

```java
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository = new MemoryMemberRepository();

```

MemoryMemberRepository를 변경하기 위해서 MemberServiceImpl를 변경해야 된다 ⇒ ocp 위반 

MemberServiceImpl(구현체)가 MemoryMemberRepository(구현체)에 의존한다 → MemberRepository(interface)에 의존하게 변경해야함 → DIP 위배 

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/7c1cab86-6c85-4c5e-bd95-612cf9aa0892/Untitled.png)

## 세션[3] 스프링 핵심 원리 이해2 - 객체 지향 원리 적용

---

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/4cad04f2-ea3a-4a72-a86d-761dbef2a123/Untitled.png)

### AppConfig 역할

- 애플리케이션의 전체 동작 방식을 구성(config)하기 위해, 구현 객체를 생성하고, 연결하는 책임을 가지는 별도의 설정 클래스
    - 구성 정보에서 역할과 구현을 명확하게 분리, 역할이 잘 들어나고 중복을 제거
    - **구현객체를 생성**
        
        MemberServiceImpl
        MemoryMemberRepository
        
    - 구현 객 인스턴스의 참조를 생성자를 통해 주입
        
        MemberServiceImpl → MemoryMemberRepository
        
    - 이제 할인 정책을 변경해도, 애플리케이션의 구성 역할을 담당하는 AppConfig만 변경하면 된다. 클라이언트 코드인 OrderServiceImpl 를 포함해서 사용 영역의 어떤 코드도 변경할 필요가 없다.
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/4cad04f2-ea3a-4a72-a86d-761dbef2a123/Untitled.png)
    

### OCP, DIP 위반 예제와 해결 방법

- 위반 예제
    
    ```java
    public class MemberServiceImpl implements MemberService{
        private final MemberRepository memberRepository ;
        private final MemberRepository memberRepository = new MemoryMemberRepository();
        @Override
        public void join(Member member) {
            memberRepository.save(member);
        }
    }
    ```
    
    - OCP 위반 - MemberServiceImpl 클래스가 MemoryMemberRepository의 구현체의 의존함
    MemberRepository를 다른 구현체로 변경하거나 확장하기 위해선 MemberServiceImpl 클래스를 수정해야함
    - DIP 위반 - MemberServiceImpl 클래스는 MemberRepository 인터페이스가 아닌 MemoryMemberRepository 구체적인 구현 클래스에 의존함
- 해결 예제
    
    ```java
    @Configuration // CGLIB 상속 ( 실행중인 인스턴스면 재활용, 실행중이지 않으면 new 생성)
    public class AppConfig {
    
        @Bean
        public MemberService memberService() {
            System.out.println("Call AppConfig.memberService");
            return new MemberServiceImpl(memberRepository());
        }
    
        @Bean
        public MemberRepository memberRepository() {
            System.out.println("Call AppConfig.memberRepository");
            return new MemoryMemberRepository();
        }
    
        @Bean
        public OrderService orderService() {
            System.out.println("Call AppConfig.orderService");
            return new OrderServiceImpl(memberRepository(), discountPolicy());
        }
    
    //  할인 정책을 바꾸고 싶으면 여기서 RateDiscountPolicy를 FixDiscountPolicy로만 바꾸면됨
        @Bean
        public DiscountPolicy discountPolicy() {
            return new RateDiscountPolicy();
        }
    }
    ```
    
    Spring의 DI(Dependency Injection)을 사용하여 MemberRepository를 주입받도록 변경하면 OCP를 준수할 수 있습니다. 이렇게 하면 MemberRepository의 다른 구현체를 사용하려면 MemberServiceImpl 클래스를 수정하지 않고도 DI 설정을 변경하여 확장할 수 있음
    
    - **해결 예제 (스프링 컨테이너 사용)**
        
        ```java
        @Service
        public class MemberServiceImpl implements MemberService {
            private final MemberRepository memberRepository;
        
            // 생성자를 통해 의존성 주입
            @Autowired
            public MemberServiceImpl(MemberRepository memberRepository) {
                this.memberRepository = memberRepository;
            }
        
            @Override
            public void join(Member member) {
                memberRepository.save(member);
            }
        }
        ```
        
        - **스프링 컨테이너 사용**: `AnnotationConfigApplicationContext`를 사용하여 스프링 컨테이너를 초기화하고, 설정 클래스로 `AppConfig`를 지정합니다.
        - **빈 등록**: `AppConfig` 클래스에 `@Bean` 애노테이션을 사용하여 `MemberRepository`와 `MemberService` 빈을 등록합니다.
        - **의존성 주입**: `@Autowired` 애노테이션을 사용하여 `MemberServiceImpl` 클래스의 생성자에 `MemberRepository`를 주입합니다.
        
        이렇게 하면 스프링 컨테이너를 사용하여 DIP와 OCP를 만족하면서 의존성을 관리할 수 있습니다. 새로운 `MemberRepository` 구현체를 도입하거나 변경할 때는 `AppConfig` 클래스만 수정하면 됩니다.
        
    - 스프링 컨테이너 적용
        
        ```java
        public class MemberApp {
            public static void main(String[] args) {
        //        AppConfig appConfig = new AppConfig();
        //        MemberService memberService = appConfig.memberService();
        
                ApplicationContext applicationContext = new AnnotationConfigReactiveWebApplicationContext(AppConfig.class);
                MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        
                Member member = new Member(1L, "memberA", Grade.VIP);
                memberService.join(member);
        
                Member findMember = memberService.findMember(1l);
                System.out.println("new member = " + member.getName());
                System.out.println("findMember = " + findMember.getName());
            }
        }
        ```
        
        - ApplicationContext 를 스프링 컨테이너라 한다.
        기존에는 개발자가 AppConfig 를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를통해서 사용한다.
        - 스프링 컨테이너는 @Configuration 이 붙은 AppConfig 를 설정(구성) 정보로 사용한다. 여기서 @Bean 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다.
        - 스프링 빈은 @Bean 이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다. ( memberService ,
        orderService )
        - 이전에는 개발자가 필요한 객체를 AppConfig 를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾아야 한다. 스프링은 applicationContext.getBean() 메서드를 사용해서 찾을 수 있다.
        - 기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이제부터는 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.

### 스프링 컨테이너(ApplicationContext)와 빈

1. **스프링 컨테이너 초기화**: 애플리케이션이 시작되면서 스프링 컨테이너(ApplicationContext)가 초기화됩니다.
2. **빈 스캔 및 등록**: 
    - **자동 등록(@Component)**
        - **스캔**
            
            `@ComponentScan`(@SpringbootApplication안에 포함) 애노테이션을 통해 지정된 패키지를 스캔하면서, 베이스 패키지와 그 하위 패키지에서 @Component(@Service,@Controller@Configuration)들을이 붙은 클래스를 스캔합니다.
            
        - **등록**
            
            스프링 컨테이너는 찾아낸 클래스 자체의 인스턴스를 생성하고, 이 인스턴스를 스프링 컨테이너에 빈으로 등록합니다.
            
    - **명시적 등록(@Bean)**
        - **스캔**
            1. 스프링 컨테이너가 `@Configuration` 애노테이션이 붙은 클래스들을 찾아서 인스턴스화하고 빈으로 등록합니다. 
            2. `@Configuration` 클래스 내부의 `@Bean` 애노테이션이 붙은 메서드들을 호출합니다. 
            3. 각 메서드는 객체를 생성하여 반환하며, 이 객체들은 스프링 컨테이너에 빈으로 등록됩니다. 
            4. 메서드 이름이 빈의 이름으로 사용됩니다.
        - **등록**
            
            `@Configuration` 클래스 내부에서 사용되며 메서드 레벨에서 사용됩니다.
            
            해당 메서드가 반환하는 객체를 스프링 컨테이너에 빈으로 등록합니다. 
            
            주로 메서드로 객체를 전달하여 스프링 컨테이너가 해당 객체를 빈으로 등록합니다 
            
3. **빈 관리**: 생성된 객체는 스프링 컨테이너에 의해 관리되며, 필요할 때마다 의존성을 주입하고 사용할 수 있습니다.
    - `@Autowired`를 통해 의존성을 주입
        
        ```java
        
        @Component
        public class MyComponent {
        
            private final MyService myService;
        
            @Autowired
            public MyComponent(MyService myService) {
                this.myService = myService;
            }
        
            public void doSomething() {
                myService.performService();
            }
        }
        ```
        

- applicationContext.getBean() - 특정 빈을 명시적으로 가져오는 방법
    
    ```java
    public class MyApp {
        public static void main(String[] args) {
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            MyService myService = context.getBean(MyService.class);
            myService.performService();
        }
    }
    ```
    

### 좋은 객체 지향 설계의 5가지 원칙의 적용

- SRP 단일 책임 원칙 - 한 클래스는 하나의 책임만 가져야 한다.
    - Appconfig가 객체를 생성하고 연결하는 책임을 전부짐
- DIP 의존관계 역전 원칙 - 추상화에 의존해야지, 구체화에 의존하면 안된다
    - AppConfig가 FixDiscountPolicy 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드에 의
    존관계를 주입했다. 이렇게해서 DIP 원칙을 따르면서 문제도 해결
- OCP - 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다
    - AppConfig가 의존관계를 FixDiscountPolicy RateDiscountPolicy 로 변경해서 클라이언트 코드
    에 주입하므로 클라이언트 코드는 변경하지 않아도 됨
    

### **프레임워크 vs 라이브러리**

- 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. (JUnit)
- 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다

### 제어의 역전 IoC(Inversion of Control)

- 기존 프로그램은 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고, 연결하고, 실행했다. 한마디로 구현 객체가 프로그램의 제어 흐름을 스스로 조종했다. 개발자 입장에서는 자연스러운 흐름이다.
- 반면에 AppConfig가 등장한 이후에 **구현 객체는 자신의 로직을 실행하는 역할**만 담당한다. **프로그램의 제어 흐름은 이제 AppConfi**g가 가져간다. 예를 들어서 OrderServiceImpl 은 필요한 인터페이스들을 호출하지만어떤 구현 객체들이 실행될지 모른다.
- 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 심지어 OrderServiceImpl 도
AppConfig가 생성한다. 그리고 AppConfig는 OrderServiceImpl 이 아닌 OrderService 인터페이스의
다른 구현 객체를 생성하고 실행할 수 도 있다. 그런 사실도 모른체 OrderServiceImpl 은 묵묵히 자신의 로직을 실행할 뿐이다.
- 이렇듯 프로그램의 제어 흐름을 **직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)**이라 한다.

### 의존관계 주입

애플리케이션 **실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것**을 **의존관계 주입**이라 한다.

의존관계는 정적인 클래스 의존 관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계 둘을 분리해서 생각해야 한다.

- 정적인 클래스 의존 관계
    
    클래스가 사용하는 import 코드만 보고 의존관계를 쉽게 판단할 수 있다. 정적인 의존관계는 애플리케이션을 실행하지않아도 분석할 수 있다
    
    아래 코드에서 OrderServiceImpl은 MemberRepository, DiscountPolicy를 의존하지만 어떤 구현 객체가 OrderServiceImpl에 주입될지 모른다 (정적 클래스 의존관계)
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/d0b165fb-a7b0-4148-b515-dd6204ca07a2/Untitled.png)
    
    ```java
    public class OrderServiceImpl implements OrderService{
    
        private final MemberRepository memberRepository;
        private final DiscountPolicy discountPolicy;
     }
    ```
    
- 동적인 객체(인스턴스) 의존 관계
    - 애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존 관계다.
    - 애플리케이션 **실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것**을 **의존관계 주입**이라 한다.
    - 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
    - 의존관계 주입을 사용하면 **클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.**
    - 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.
    
    ```java
        @Bean
        public OrderService orderService() {
            System.out.println("Call AppConfig.orderService");
            // 의존 관계 주입 구현체 memberRepository,discountPolicy를 클라이언트에 전달
            return new OrderServiceImpl(memberRepository(), discountPolicy());
        }
    ```
    

### 스프링 컨테이너 = IoC 컨테이너 = DI 컨테이너

- AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을 IoC 컨테이너 또는 DI 컨테이너라 한다.
- 의존관계 주입에 초점을 맞추어 최근에는 주로 DI 컨테이너라 한다. 또는 어샘블러, 오브젝트 팩토리 등으로 불리기도 한다.

## 세션[4] 스프링 컨테이너와 스프링 빈(20240723)

---

### 스프링 컨테이너와 빈

스프링 컨테이너는 스프링 빈의 생성과 관리를 담당하며, 다양한 설정 형식을 지원

1. **스프링 컨테이너 생성**
    - `new AnnotationConfigApplicationContext(AppConfig.class)`와 같이 자바 설정 클래스(AppConfig)를 기반으로 생성 → 스프링 컨테이너 생성(빈 저장소 생성)
        
        ```java
        ApplicationContext applicationContext = new AnnotationConfigReactiveWebApplicationContext(AppConfig.class);
        ```
        
        - ApplicationContext : 인터페이스이자 스프링 컨테이너
        - AnnotationConfigReactiveWebApplicationContext : 구현체
        - AppConfig : 자바 설정 클래스로 스프링 컨테이너의 구성정보
    - XML 설정 파일을 사용하는 경우 `GenericXmlApplicationContext` 사용
2. **스프링 빈 등록**
    - 스프링 컨테이너가 설정 클래스의 메서드(Appconfig)의 @Bean 태그가 붙은 메서드들을 호출해서 결과를 빈 저장소에 저장
    - 빈 이름은 메서드 이름을 사용하며, `@Bean(name="customName")`으로 지정 가능
        - **Bean 이름은 중복되어선 절대 안된다**
3. **스프링 빈 의존관계 설정**
    - 설정 정보를 참고하여 의존관계 주입(DI) 처리
        
        ```java
            // AppConfig.class
            @Bean
            public MemberService memberService() {
                System.out.println("Call AppConfig.memberService");
                return new MemberServiceImpl(memberRepository());
            }
        
            @Bean
            public MemberRepository memberRepository() {
                System.out.println("Call AppConfig.memberRepository");
                return new MemoryMemberRepository();
            }
        
            @Bean
            public OrderService orderService() {
                System.out.println("Call AppConfig.orderService");
                return new OrderServiceImpl(memberRepository(), discountPolicy());
            }
        
            // FixDiscountPolicy 정책을 변경할 수 있ㄷㅏ
            @Bean
            public DiscountPolicy discountPolicy() {
                //return new FixDiscountPolicy();
                return new RateDiscountPolicy();
            }
        ```
        
    - 생성자 주입과 세터 주입 등 다양한 방식 지원

### 스프링 빈 조회

1. **모든 빈 조회**
    
    ```java
    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
    
            //Role ROLE_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("Name = " + beanDefinitionName + "object = " + bean);
            }
        }
    }
    ```
    
    - `ac.getBeanDefinitionNames()`로 모든 빈 이름 조회
    - 빈 구분
        - `Role ROLE_APPLICATION`: 직접 등록한 애플리케이션 빈
        - `Role ROLE_INFRASTRUCTURE`: 스프링이 내부에서 사용하는 빈
2. **특정 빈 조회**
    
    ```java
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
    
        @Test
        @DisplayName("빈 이름으로 조회")
        void findBeanByName() {
            MemberService memberService = ac.getBean("memberService", MemberService.class);
            assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        }
    
        @Test
        @DisplayName("이름없이 타입으로 조회")
        void findBeanByType() {
            MemberService memberService = ac.getBean(MemberService.class);
            assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        }
    		// 구현체로 조회는 웬만하면 하지 말자 
        @Test
        @DisplayName("구체 타입으로 조회")
        void findBeanByName2() {
            MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
            assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
        }
    
        @Test
        @DisplayName("빈 이름으로 조회 실패 테스트")
        void findBeanByNameX() {
            assertThrows(NoSuchBeanDefinitionException.class,
                    () -> ac.getBean("xxxxx", MemberService.class));
        }
    ```
    
    - `ac.getBean(beanName, 타입)`으로 빈 이름과 타입을 지정하여 조회
        - 빈 이름, 타입 둘 중 하나 만으로도 조회 가능
    - 타입만으로 조회할 경우 `NoSuchBeanDefinitionException` 예외 발생 가능
3. **동일한 타입이 둘 이상**
    
    ```java
    // config 파일의 아래 처럼 타입이  MemberRepository가 두개인 경우 
    @Bean
    public MemberRepository memberRepository1() {
        return new MemoryMemberRepository();
      }
    
    @Bean
    public MemberRepository memberRepository2() {
        return new MemoryMemberRepository();
    }
    ```
    
    - ac.getBean(타입)으로 조회 시 중복 오류 발생 가능
        - ac.getBean(빈 이름,타입)으로 해결 가능
    - `ac.getBeansOfType(타입)`으로 해당 타입의 모든 빈 조회 가능
- 상속관계 빈 조회
    
    부모 타입으로 조회하면, 자식 타입도 모두 함께 조회한다 
    

### BeanFactory와 ApplicationContext

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/720abf95-7e09-46c8-8ccd-ff391e9ecc90/72c6cc4e-700d-4dbb-9f06-a7995b5649d6/Untitled.png)

- **BeanFactory**
    - 스프링 컨테이너의 최상위 인터페이스
    - 빈을 관리하고 조회하는 기본 기능 제공
- **ApplicationContext - 대부분 이거 사용**
    - BeanFactory를 상속받아 더 많은 부가 기능 제공
    - 메시지소스를 활용한 국제화, 환경변수(개발,운영)등 을 구분하여 관리, 애플리케이션 이벤트, 편리한 리소스 조회 등

### 다양한 설정 형식 지원

- **자바 코드 설정**
    - `AnnotationConfigApplicationContext(AppConfig.class)` 사용
- **XML 설정**
    - `GenericXmlApplicationContext("appConfig.xml")` 사용
    - XML 설정 정보와 자바 코드 설정 정보는 유사

### 스프링 빈 설정 메타 정보 - BeanDefinition

- 스프링 컨테이너가 자바 코드(AppConfig)를 읽어서 `BeanDefinition` 를 만듬
- 빈에 대한 메타 정보들이 있음
- XML, 자바 코드 등 다양한 설정 형식을 `BeanDefinition`으로 추상화하여 사용 - 이렇게만 알고있자

---

## 세션[5] 싱글톤 컨테이너

---

## 세션[6] 컴포넌트 스캔

---

## 세션[7] 의존관계 자동 주입

---

## 세션[8] 빈 생명주기 콜백

---

## 세션[9] 빈 스코프