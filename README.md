# 제목 뭐로 할까

## 참고 블로그

[스프링 부트 테스트](https://brunch.co.kr/@springboot/207)

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
