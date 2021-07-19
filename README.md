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