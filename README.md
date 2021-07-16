# 제목 뭐로 할까

## 참고 블로그

[스프링 부트 테스트](https://brunch.co.kr/@springboot/207)

## 이슈

### builder 패턴 적용 (롬복의 @Builder 사용)

### view를 위한 Form 객체에 @Setter를 만들지 않음

* Form 용도의 객체를 모델에 주면 view template에서 객체의 필드에 해당하는 변수를 객체의 **setter**메소드를 이용해서 설정한다.
* **setter** 메소드가 없으니 `MemberForm`객체의 필드가 모두 null값인 채로 나온다.
