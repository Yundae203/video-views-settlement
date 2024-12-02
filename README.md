# 정산 프로젝트
2024.10.23 ~ 2024.11.12 (4주)

> 영상 시청 로그를 기록한 뒤, 매일 누적된 로그들을 각 영상별로 **Spring Batch**를 통해 정산하여   
> 창작자의 영상에 대한 하루 수입과 영상 데이터 통계를 제공하는 프로그램입니다.



![image](https://img.shields.io/badge/SPRING_BOOT-6DB33F?style=for-the-badge&logo=springboot&logoColor=FFFFFF)
![image](https://img.shields.io/badge/SPRING_BATCH-6DB33F?style=for-the-badge&logo=Spring&logoColor=FFFFFF)
![image](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=FFFFFF)
![image](https://img.shields.io/badge/Querydsl-0769AD?style=for-the-badge&logoColor=FFFFFF)
![image](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=FFFFFF)
![image](https://img.shields.io/badge/REDIS-FF4438?style=for-the-badge&logo=Redis&logoColor=FFFFFF)
![image](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=FFFFFF)


## 주요 기능
- **영상 업로드 및 시청**
    - **Redis**와 **Scheduler**를 사용하여  방식으로 영상 조회수 1분마다 배치 업데이트
    - **Redis**의 **TTL**을 활용하여 어뷰징 방지
    - **Redisson**을 활용하여 시청 발생 영상 기록 시 동시성 이슈 방지
    
- **영상 조회수에 따른 수입 정산 및 조회**
    - **Spring Batch**의 **Partioner**를 활용하여 **Multi-Thread** 방식으로 **데이터 1억 건** 정산에 **4m 16s 321ms 소요**
    - **Excution Context**에 **Chunk** 단위로 실패 지점 저장 후 재실행


## 🚀 정산 성능 개선

**📁 데이터량** 

- **영상 시청 기록** : 500만 건 
- **영상** : 6334 개 

> ✒[시청 기록 대비 영상 수 계산 근거](https://www.notion.so/yun-seok/500-149b3309ca9480d0836ee14a501ecab7)
 
 **⏱️ 개선 전/후 소요 시간**

 | 개선 단계    | 소요 시간            | 성능 개선율  |
 |---------------|----------------------|--------------|
 | 개선 전       | 3h 27m 57s 358ms    | -            |
 | **1차 개선**  | 1h 11m 16s 772ms    | **65.6%**    |
 | **2차 개선**  | 2m 57s 582ms        | **95.8%**    |
 | **3차 개선**  | **43s 654ms**       | **99.7%**    |
---

- **[ 1차 개선 ]** 조회가 발생한 영상만 체크하여 **전체 영상의 32%만 배치 처리**
    - **소요 시간** : 1h 11m 16s 772ms
    - [유튜브 리서치](https://www.intotheminds.com/blog/en/research-youtube-stats/)에 따르면 하루에 68% 영상은 조회가 발생하지 않음  

- **[ 2차 개선 ]** (시청 일자 → 영상 번호) 순으로 **복합 Index 설정**
    - **소요 시간** : 2m57s582ms
    - 영상 번호의 **cardinality**가 더 높지만, **추후 partitioning 할 것을 고려하여** (시청 일자 → 영상 번호) 순으로 Index 설정
        - **개선 전** | 인덱스 타입 **range**, 실행 계획 소요 시간 **1312 ms**
        - **개선 후** | 인덱스 타입 **index_merge**, 실행 계획 소요 시간 **3.5 ms ( 약 99% 성능 개선 )**

- **[ 3차 개선 ]** 배치 작업 **Partioning**을 통한 **Multi-thread 설정**
    - **소요 시간** : 43s 654ms
    - **PartitionHandler** | GridSize : 6
    - **ThreadPoolTaskExecutor** | CorePoolSize : 6, MaxPoolSize : 6


## 🚨 트러블 슈팅

## 1. 데이터량 증가에 따른 DB 집계 성능 저하 문제
### 문제 상황 & 분석

전체 데이터 **500만 건 중, 4천 건**에 대해 `SUM` 함수 실행 시, **48ms가 소요**되었습니다.

유튜브 일일 영상 최대 조회 수 **‘BTS butter’**가 **1억 820만 뷰를 기록**한 것을 근거로,

데이터를 **1억 3천만까지 증가 후 13만 건에 대해** `SUM`**을 진행**해보았습니다. 

실행 결과 데이터를 집계하는데 총 **13568ms가 소요되며, 약 282배 증가**하였으며

이를 바탕으로 **데이터량 증가에 따라 처리 시간이 증가**함을 알 수 있었습니다.

> **참고자료 :** https://www.yna.co.kr/view/AKR20210523043100005
> 

### 해결 방안

대용량 데이터를 DB에서 집계할 때 성능이 저하되는 원인은, 많은 **데이터를 한 번에 스캔한 뒤 작업을 진행**하기 때문입니다.

```bash
TYPE: ref, 48ms + 네트워크 통신 소요
-> Aggregate: count(cpwh.id), sum(cpwh.ad_views), sum(cpwh.play_time)  (cost=5661 rows=1) (actual time=48.7..48.7 rows=1 loops=1)
    -> Index lookup on cpwh using idx_content_post_id_watched_at (content_post_id=10, watched_at=DATE'2024-11-02')  (cost=4526 rows=4923) (actual time=0.589..48.4 rows=4923 loops=1)

TYPE: ref, 13.5초 + 네트워크 통신 소요
-> Aggregate: count(cpwh.id), sum(cpwh.ad_views), sum(cpwh.play_time)  (cost=328022 rows=1) (actual time=13568..13568 rows=1 loops=1)
    -> Index lookup on cpwh using idx_content_post_id_watched_at (content_post_id=10, watched_at=DATE'2024-11-02')  (cost=270977 rows=247572) (actual time=0.305..13555 rows=134790 loops=1)
```

실행 계획을 살펴보면, **데이터가 늘어나면,** **Index lookup 시간이 늘어나는 것을 확인**할 수 있었습니다.

따라서, 데이터를 한 번에 처리하지 않고, 부분적으로 나누어서 처리해야 함을 알 수 있었습니다.

이를 실천하기 위해서 두 가지 방법을 선정했습니다.

1. `FROM` 절 **서브쿼리를 통해 데이터 부분 집계** 후, 어플리케이션 단에서 최종 집계
2. **페이징을 통해** 데이터를 어플리케이션 단으로 불러온 후 **부분 집계** 후, 최종 집계

### 의사 결정

프로젝트에서는 **Querydsl을 사용**하고 있습니다.

**Querydsl은** `FROM` **절의 서브쿼리를 공식적으로 지원하고 있지 않습니다.**

`@Subselect`을 통해 `FROM` 절 서브쿼리를 구현할 수 있으나,

이는 컴파일 시점에 오류를 잡을 수 있으며, 동적으로 쿼리를 제어할 수 있다는 **Querydsl의 장점을 해치는 행위**입니다.

따라서, 최종적으로 **페이징을 통해 문제를 해결**하는 것으로 결정했습니다.

이를 통해, **Querydsl**의 장점은 살리고 확성을 갖추면서도 **안정적으로 데이터를 리드**할 수 있게 되었습니다.

데이터 페이징 이후, 바로 부분 집계를 수행한 뒤, **데이터의 참조를 끊어** **OOM 문제 역시 해결**하였습니다. 

### 해결 결과

#### SQL

```sql
explain analyze select ad_views, play_time 
from content_post_watch_history cpwh 
where content_post_id = 10
and watched_at = '2024-11-02'
and id > 1000
limit 1000;
```

#### 데이터량에 따른 페이징 수행 시간 비교

```bash
데이터: 4천 건, TYPE: index_merge, (3ms + 네트워크 통신) * 4 소요
-> Limit: 1000 row(s)  (cost=2328 rows=1000) (actual time=0.0585..3.24 rows=1000 loops=1)
    -> Filter: ((cpwh.watched_at = DATE'2024-11-02') and (cpwh.content_post_id = 10) and (cpwh.id > 1000))  (cost=2328 rows=2461) (actual time=0.0576..3.19 rows=1000 loops=1)
        -> Intersect rows sorted by row ID  (cost=2328 rows=2461) (actual time=0.0546..3.08 rows=1000 loops=1)
            -> Index range scan on cpwh using idx_content_post_id_watched_at over (content_post_id = 10 AND watched_at = '2024-11-02' AND 1000 < id)  (cost=0.00238..11.7 rows=4922) (actual time=0.0471..0.333 rows=1000 loops=1)

데이터: 13만 건, TYPE: index_merge, (3ms + 네트워크 통신) * 134 소요
-> Limit: 1000 row(s)  (cost=135168 rows=1000) (actual time=0.0551..3.39 rows=1000 loops=1)
    -> Filter: ((cpwh.watched_at = DATE'2024-11-02') and (cpwh.content_post_id = 10) and (cpwh.id > 1000))  (cost=135168 rows=123764) (actual time=0.0545..3.33 rows=1000 loops=1)
        -> Intersect rows sorted by row ID  (cost=135168 rows=123765) (actual time=0.0521..3.22 rows=1000 loops=1)
            -> Index range scan on cpwh using idx_content_post_id_watched_at over (content_post_id = 10 AND watched_at = '2024-11-02' AND 1000 < id)  (cost=0.00254..630 rows=247530) (actual time=0.043..0.34 rows=1000 loops=1)
```

### 정리

- `OFFSET` 대신 `id > last_id` 를 사용하여, 데이터량에 상관없이 일정한 속도가 나오도록 **쿼리 튜닝을 진행**했습니다.
- 데이터량과 상관 없이 **일관되게 3ms 정도로 빠른 조회 속도**를 보여주었습니다.


## 2. 트랜잭션 전파로 인한 `Undo Log` 과적 문제
### 문제 상황 & 분석

청크 작업 중, **프로세서**에서 **item**(영상) 별로 시청 기록을 조회할 때, **뒤로 갈수록** **조회 속도가 떨어지는 문제를 발견**했습니다. 

`SUM` 에서 페이징으로 바꾼 후, 집계가 아닌 데이터 조회로 로직이 변경된 것이 원인이었습니다.

**MySQL**의 **InnoDB**는 **MVCC**를 구현하기 위해서 **같은 트랜잭션 내에서 발생한** `SELECT`**에 대해** `Undo Log`**를 생성**합니다.

스프링 배치는 청크 단위로 트랜잭션이 관리됩니다.

따라서, 청크 크기만큼 **프로세서의** `SELECT` **이 반복될 때,** `Undo Log`**가 그만큼 생성되어 누적**되고 있었고,

그에 따라 청크가 커밋되기 전까지 프로세서 내 조회 속도가 **51ms에서 1185ms 까지( 약 23배 )** **선형적으로 상승**했습니다.

### 해결 방안
#### 1. 청크 사이즈 감소
청크 사이즈를 줄여, 프로세서의 실행 횟수를 줄이면, `Undo Log`의 누적 수가 줄어들어 성능이 개선됩니다.

#### 2. 트랜잭션 전파 행위 
프로세서의 트랜잭션을 청크 단위의 상위 트랜재션과 분리하면, `Undo Log`의 생성을 제어할 수 있습니다.

상위 트랜잭션과 분리하기 위해서는 아래와 같은 2가지 선택지가 있습니다.
- Propagation.REQUIRES_NEW
- Propagation.NOT_SUPPORTED

### 의사 결정

**청크 사이즈를 줄이는 것은 근본적인 해결책이 되지 못합니다.**

우선, 청크 사이즈가 줄어 배치 처리를 위해 **전체적인 리드 횟수가 증가하여 성능이 저하**됩니다.

시청 기록 수가 늘어나 **페이징 횟수가 늘어나게 되면** 역시 `Undo Log`가 쌓여 역시 **성능 저하가 발생**합니다.


따라서, **전파 행위를 제어하기로 최종적으로 의사결정** 했습니다.

프로세서에서 읽어오는 로그 데이터는 **불변 데이터이기 때문에, 따로 트랜잭션이 필요하지 않았습니다.**

그렇기 때문에, 트랜잭션이 생성하지 않는 **Propagation.NOT_SUPPORTED가 더 적절한 설정**이라 판단했습니다.

### 해결 결과
- 조회 시간이 청크 전체에서 **일관되게 55ms 이내로 유지**되며, **모든 조회에 평균 1분 소요 약 91% 성능 개선**

## ✈ 프로젝트 상세 내용
### 기술 스택
**[ Java 21 ], [ Spring Boot 3.3.4 ], [ Spring Batch 5.1.2 ], [ JPA ], [ Querydsl ], [ MySQL 9 ], [ Redis ]**

### ERD
![image](https://github.com/user-attachments/assets/1f2bca38-3285-4fd2-8ee7-58e62251f0cc)


