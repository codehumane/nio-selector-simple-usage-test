# nio-selector-simple-usage-test

[NIO](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)의 [Selector](https://docs.oracle.com/javase/9/docs/api/java/nio/channels/Selector.html), [Channel](https://docs.oracle.com/javase/9/docs/api/java/nio/channels/Channel.html), [SelectionKey](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html)에 대해 알아보기 위해 작성한 간단한 코드와 관련된 개념 정리

## Nonblocking System Architecture

![nonblocking system architecture](http://www.onjava.com/2002/09/04/graphics/Fig1.gif)

*그림 출처: http://www.onjava.com/pub/a/onjava/2002/09/04/nio.html?page=2

| 요소                                                         | 설명                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| SERVER                                                       | 요청을 받는 어플리케이션                                     |
| CLIENT                                                       | 서버에 요청을 전달하는 어플리케이션 집합                     |
| [CHANNEL](https://docs.oracle.com/javase/9/docs/api/java/nio/channels/Channel.html) | 클라이언트와 서버 간의 커뮤니케이션 채널. 서버 IP 주소와 포트 번호로 식별됨. 클라이언트는 이 각각의 채널을 통해 데이터를 READ & WRITE. 데이터들은 버퍼 아이템에 담겨 사용됨. |
| [SELECTOR](https://docs.oracle.com/javase/9/docs/api/java/nio/channels/Selector.html) | 채널들의 상태를 모니터링하다가 연결 혹은 데이터 입출력 요청이 들어오면, 이를 KEY로 만들어 서버에 전달. |
| [KEY](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html) | 요청을 정렬하기 위해 SELECTOR에 의해 사용되는 객체. 각 KEY는 단일 클라이언트의 부분 요청을 나타내며, 클라이언트는 누구이고 요청의 타입은 무엇인지 알려주는 식별정보가 담김. |

요청이 처리되는 과정은 다음과 같음.

- 클라이언트들이 동시다발적으로 요청을 서버에게 던짐.
- 이들 요청/응답은 **CHANNEL**을 통해 오고감.
- **SELECTOR**는 이들 채널을 모니터링하다가,
- 관심 이벤트([ACCEPT](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html#OP_ACCEPT), [CONNECTION](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html#OP_CONNECT), [READ](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html#OP_READ), [WRITE](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html#OP_WRITE))가 발생하면 이를 서버 프로세스에게 전달함.
- 이때 사용되는 것이 바로 **KEY**([SelectinoKey](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/SelectionKey.html)의 인스턴스).
- 이들 key에는 요청을 만든 클라이언트와 요청 타입 정보가 담김.
- 언뜻 보기에는 블럭킹 시스템 같지만, **KEY**에는 클라이언트가 보낸 정보가 일정 크기로 나뉘어 담김.
- 또한 적절한 시분할<sup>time-sharing</sup> 정책에 따라 처리됨.

## 왜 Selector를 사용하는가?

- 여러 채널을 관리하는 데 Selector는 한 개의 스레드만을 사용.
- 컨텍스트 스위칭이 없으며, 메모리와 같은 리소스 사용도 절약.
- [Java NIO Selector의 Why Use a Selector](http://tutorials.jenkov.com/java-nio/selectors.html#why-use-a-selector) 참고.
- [여기]()http://javacan.tistory.com/entry/87)의 `논블럭킹 모드 사용하기`도 함께 참고.

