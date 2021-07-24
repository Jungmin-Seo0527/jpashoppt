# 제목 뭐로 할까

## 참고 블로그

* [스프링 부트 테스트](https://brunch.co.kr/@springboot/207)
* [Spring Guide - 예외 처리 전략](https://www.popit.kr/spring-guide-%EC%97%90%EC%99%B8-%EC%B2%98%EB%A6%AC-%EC%A0%84%EB%9E%B5/)
* [IntelliJ 디버깅 해보기](https://jojoldu.tistory.com/149)

## 이슈

### builder 패턴 적용 (롬복의 @Builder 사용)

### view를 위한 Form 객체에 @Setter를 만들지 않음

* Form 용도의 객체를 모델에 주면 view template에서 객체의 필드에 해당하는 변수를 객체의 **setter**메소드를 이용해서 설정한다.
* **setter** 메소드가 없으니 `MemberForm`객체의 필드가 모두 null값인 채로 나온다.

### Update - entity의 field값 변경하기(일부 수정)

`updateItemDto`객체로 `item`엔티티에서 수정할 값들을 받아왔다.        
서비스 계층에서 수정할 `item`엔티티를 `id`로 찾아서 각 필드를 dto의 해당 필드값으로 수정했다. 일괄적인 변경을 하기위해 `Item`클래스에서 `update`메소드를 만들었다.

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    // ...
    @Transactional
    public void updateItem(Long itemId, UpdateBookDto dto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id=" + itemId));
        if (item.getClass() == Book.class) {
            ((Book) item).update(dto.getName(), dto.getPrice(), dto.getStockQuantity(), dto.getAuthor(), dto.getIsbn());
        }
    }
}
```

```java
public class Item {

    // ...

    public void update(String name, int price, int stockQuantity) {
        setName(name);
        setPrice(price);
        setStockQuantity(stockQuantity);
    }
}
```

* 모든 도메인은 `setter`의 AccessLevel을 `PRIVATE`로 설정했다.

하지만 뭔가 이상하다. `repository`계층에서 CRUD를 관리해야 하는 게 아닌가?      
`update`메소드가 도메인에 존재해도 되는가에 대한 의문이 생긴다. update는 repository에 책임이 있는 것이 좋은것이 아닌가 하는 생각이 든다.(물론 `update` 메소드 자체가 `setter`
사용을 최소한으로 하기 위해 만든 의미있는 메소드 이다.)

우선 지금 내가 생각한 것은 동적 쿼리를 이용해서 `ItemRepository`에서 `update`메소드를 만들어 주는 것이다. DTO는 엔티티의 모든 필드를 필드로 가지고 수정할 필드는 값이 존재, 수정하지 않을
필드는 null값이 채워져 있을 것이다. 동적 쿼리를 이용해서 null값이 아닌 DTO의 필드값을 엔티티의 필드 값으로 수정하는 것이다.

또한 만약 필드가 100개가 존재해도 `update`메소드는 100개의 필드값을 파라미터로 가져야 한다. 분명히 내가 한 방법은 좋은 방법은 아니다.

### 7.21 - Junit5 테스트 코드에서 롬복 적용

Junit5에서 롬복을 이용하려면 의존성을 추가해 주어야 한다.

```groovy
dependencies {
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
}
```

### 7.21 - Junit5 RestAPI 테스트 코드 작성

* 참고 블로그
    * [SpringBOot, Junit5에서 REST API(Controller)테스트](https://memostack.tistory.com/197)
    * [개발노트 - [JUnit] JUnit5 사용법과 TDD(2) - REST API 테스트](https://sunghs.tistory.com/138)

* REAT API 테스트 코드 작성 경험이 전무하여 무에서 시작하고 있다. (첫 테스트 코드는 성공)

### 7.21 MockMvcResultHandlers.print() 한글 깨짐 현상

* application.yml 설정 추가

```yaml
server:
  servlet:
    encoding:
      force-response: true
```

설정 이전에는 `MockHttpServletResponse`의 contentType이 `application/json` 이었다.   
설정 후에는 `application/json;charset=utf-8`로 변경되어 출력은 제대로 나온다.    
하지만 테스트를 하는 과정에서 `andExpect(content().contentType(MediaType.APPLICATION_JSON))`으로 contentType을
검사했었다. `MediaType.APPLICATION_JSON`은 문자열로 `application/json`으로 치환된다. 그러나 나는 인코딩 설정이 추가
되어서 `application/json;charset=utf-8`이 된 상태이다. 따라서 contentType 검사 코드를 문자열로 바꾸어서 진행했다.

`andExpect(content().contentType("application/json;charset=utf-8)`

### 7.21 controller exception handler

컨트롤러에서 예외가 발생하면 어떻게 처리해야 하는가.   
이미 반환 형식을 정했기 때문에 해당 객체가 JSON으로 변환해서 반환되어야 하는데 예외가 발생한 경우에도 같은 형식으로 반환해야 하는가?

```java
@RestController
@RequiredArgsConstructor
public class Controller {

    private final Service service;

    @GetMapping("/api/member/{id}")
    public MemberResponseDto findMember(@PathVariable Long id) {
        Member member = service.findMemberById(id); // 이때 Repository 계층에서 null이 반환되었다면
    }
}
```

위의 코드는 id로 맴버를 찾는 요청을 보냈지만 해당 id의 맴버가 없을 경우이다. 이때도 `MemberResponseDto`형태로 response를 보내야 하는가?

#### @ExceptionHandler

```java
public class Controller {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> handleDemoException(IllegalArgumentException e) {
        log.error("에러 발생");
        return ResponseEntity
                .status(BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body("400 error");
    }
}

```

* `@ExceptionHandler`
    * 해당 클래스 내에서 발생하는 예외를 일괄적으로 처리하는 메소드
    * 위의 코드는 데모용으로 작성한 것이고 Resolver를 이용해서 세분화된 예외 상황에 대해 각각 적절한 Response 를 날릴 수 있다.

### 7.22 @DynamicUpdate

사용자들이 자신의 정보를 수정고 싶은 경우가 있다. 비밀번호, 주소, 전화 번호 등등 가변의 개인정보들이 존재한다. 그리고 이는 대부분 정해져 있다. 그래서 엔티티에 `update`메소드를 만들고 `Member`
엔티티에서 가변의 필드들만 뽑아서 `MemberUpdateInfoDto`클래스를 만들었다.

* `Member`

```java
package jm.tp.jpashop.pt.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter @Builder @Setter(PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public void updateInfo(MemberUpdateInfoDto dto) {
        setName(dto.getName());
        setAddress(dto.getAddress());
    }
}

```

* `MemberUpdateInfoDto`

```java
package jm.tp.jpashop.pt.model;

import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter @Setter(PRIVATE) @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberUpdateInfoDto {

    private String name;
    private Address address;

    public static MemberUpdateInfoDto create(MemberApiDto dto) {
        return MemberUpdateInfoDto.builder()
                .name(dto.getName())
                .address(Address.builder()
                        .city(dto.getAddress().getCity())
                        .street(dto.getAddress().getStreet())
                        .etc(dto.getAddress().getEtc())
                        .build())
                .build();
    }
}

```

사용자의 이름인 `name`필드만 변경하는 요청이 온다고 가정하자. 즉 `Address`는 변하지 않는다.   
만약 request 메세지가 변경하려는 정보만 JSON으로 들어오면 최고의 상황이겠지만 이렇게 되면 요청메시지를 받는 객체인 `MemberApiDto`에서 null 값이 생긴다. 그렇다면 모든 필드에 대해서 null
이 아닌 필드만 수정해야 한다. 그래서 우선은 요청 메시지에 `Member`엔티티의 모든 정보를 받되, 수정하려는 정보만 수정이 되어서 전송되는 것으로 했다.

그래고 `Member`엔티티에 `@DynamicUpdate` 애노테이션을 추가했다. 이는 변경된 필드(컬럼)에 대해서만 update 쿼리가 나가도록 한다. 즉 name 만 변경하기에 update 쿼리문은 name만
나가는 것을 기대한다.

* 기존에 존재하는 Member 정보

```json
{
  "name": "서정민",
  "address": {
    "city": "인천",
    "street": "마장로",
    "etc": "264번길"
  }
}
```

* 이름만 수정해서 요청 메시지로 보냈다.

```json
{
  "name": "서정민111",
  "address": {
    "city": "인천",
    "street": "마장로",
    "etc": "264번길"
  }
}
```

update 는 변경감지를 위해 `@Transactinal`이 존재하는 service 계층에서 엔티티 필드를 수정했다.

```java
public class Member {
    public void updateInfo(MemberUpdateInfoDto dto) {
        setName(dto.getName());
        setAddress(dto.getAddress());
    }
}
```

```java
public class MemberService {
    @Transactional
    public MemberApiDto update(Long id, MemberApiDto memberApiDto) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
        }
        member.updateInfo(MemberUpdateInfoDto.create(memberApiDto));
        return MemberApiDto.create(member);
    }
}
```

위에서 언급한대로 Member 필드의 모든 정보를 요청으로 받되 이름은 기존과는 다른 값으로 되어 있을 것이다. 모든 필드에 대해 이전값과 이후 값을 비교 한 다음 변경된 필드만 set을 해주는 방법과 어짜피
수정하지 않으려는 필드의 값은 기존 값 그대로 존재하니 모든 필드의 값을 dto 값으로 변경해주는 방법이 있다. 나는 후자를 선택했다.

그리고 update 필드를 보면 모든 컬럼에 대한 update 쿼리가 나간다.

```roomsql
    update
        member 
    set
        city=?,
        etc=?,
        street=?,
        name=? 
    where
        member_id=?
```

이는 city, street, name 이 모두 address 객체로 묶여 있으며 `MemberApiDto`를 엔티티 변경을 위한 dto인 `MemberUpdateInfoDto`로 변경하는 과정에서 Address
객체를 새로 할당 받아서 값을 넣었기에 JPA 입장에서는 조회할 때의 Address 주소값과 변경 후 주소값이 다르니 변경사항이 발생했다고 판단했다.

그래서 `Address`클래스에 `@EqualsAndHashCode`를 추가한 후에 다시 기존의 회원의 이름만 수정했다.

```roomsql
    update
        member 
    set
        name=? 
    where
        member_id=?
```

이제 변경 사항인 이름에 대해서만 update 쿼리가 나가는 것을 볼 수 있다. 만약 `etc`즉 상세주소만 변경되는 경우에는 어떻게 될까?

객체로 묶여있는 `Address`는 위와 같은 방법으로는 하나의 필드만 변경되어도 `Address`필드인 `city`, `street`, `etc` 컬럼의 update 쿼리가 나간다.

이후에 Address 필드도 Address 객체를 새로 만들어서 값을 채워 넣는 것이 아닌, 기존의 address 의 필드에 값을 넣는 방식으로 변경해도 결과는 같았다. 이 부분은 좀더 공부가 필요해 보인다.

### 7.24 데이터가 존재하지 않을 때 em.createQuery -> getSingleResult(), getResultList() 차이점

em.create에서 반환 타입을 single object, list 로 선택이 가능하다. 그런데 이 둘은 조회한 데이터가 존재하지 않을 때 큰 차이점이 존재한다.

```java
public class OrderRepository {
    public Optional<Order> findOrderItemsById(Long id) {
        return Optional.ofNullable(em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i " +
                        "where o.id = :id ", Order.class)
                .setParameter("id", id)
                .getSingleResult());
    }
}
```

위 코드의 의도는 만약 조회한 데이터가 존재하지 않으면 null값을 반환하기를 기대했다. 그래서 컨트롤러 계층에서 예외를 `@ExceptionHandler`에게 던저 주려고 했다. 하지만 위의 코드는 null값을
반환하는 것이 아닌 그 전에 예외를 터뜨린다.

#### em.createQuery("...").getSingleResult()

```java
public abstract class AbstractProducedQuery<R> implements QueryImplementor<R> {
    @Override
    public R getSingleResult() {
        try {
            final List<R> list = list();
            if (list.size() == 0) {
                throw new NoResultException("No entity found for query");
            }
            return uniqueElement(list);
        } catch (HibernateException e) {
            throw getExceptionConverter().convert(e, getLockOptions());
        }
    }
}
```

* 예외 메세지

```
javax.persistence.NoResultException: No entity found for query
```

누가 봐도 쿼리로 찾고자 하는 엔티티가 존재하지 않아서 예외를 터뜨리는 모습이다.   
코드를 보면 쿼리의 결과를 `List`로 받는데 `if(list.size() == 0)`으로 `NoResultException`을 터뜨려 버린다. 그렇다면 `getResultList()`는 어떨까???

#### em.createQuery("...").getResultList()

```java
public class ProcedureCallImpl<R>
        extends AbstractProducedQuery<R>
        implements ProcedureCallImplementor<R>, ResultContext {
    @Override
    @SuppressWarnings("unchecked")
    public List<R> getResultList() {
        if (getMaxResults() == 0) {
            return Collections.EMPTY_LIST;
        }
        try {
            final Output rtn = outputs().getCurrent();
            if (!ResultSetOutput.class.isInstance(rtn)) {
                throw new IllegalStateException("Current CallableStatement ou was not a ResultSet, but getResultList was called");
            }

            return ((ResultSetOutput) rtn).getResultList();
        } catch (NoMoreReturnsException e) {
            // todo : the spec is completely silent on these type of edge-case scenarios.
            // Essentially here we'd have a case where there are no more results (ResultSets nor updateCount) but
            // getResultList was called.
            return null;
        } catch (HibernateException he) {
            throw getExceptionConverter().convert(he);
        } catch (RuntimeException e) {
            getProducer().markForRollbackOnly();
            throw e;
        }
    }
}
```

위 코드까지 보면 `getMaxResults() == 0`인 경우에 `Collections.EMPTY_LIST`를 반환한다.   
여기서 `getMaxResult()`는 `maxRow`값을 반환한다. 즉 `getMaxResult() == 0`은 데이터가 0개가 조회되어 row 값의 max 값이 0이라는 의미이다.

> 결론    
> em.creatQuery에서 데이터가 존재하지 않을때 반환값
> * `getSingleResult()`: `NoResultException`
> * `getResultList()`: `Collections.EMPTY_LIST`;

## Note