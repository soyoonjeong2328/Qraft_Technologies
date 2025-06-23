## 기술 스택
| 사용 기술 |  버전   | 
| :---: |:-----:|
| Java |  17   |
| Gradle |       |
| Spring Boot | 3.5.3 |
| Spring WebSocket | 3.5.3 |
| SPring Data JPA | 3.5.3 |
| PostgreSQL |  15   | 
| Lombok | 최신 버전|
| JUnit + Spring Boot Test | 3.5.3포함|


## 실행방법
1. PostgreSQL (Docker Compose)
    ```
    docker compose up -d
    ```
   - 포트 : 5432
   - DB : qraft
   - 사용자 : qraftuser / qraftpass
   
<br/>
   
2. Spring Boot 서버 실행 
    ```
    ./gradlew bootRun
   ```
   또는 IDE에서 QraftTechnologiesAPplication 실행
    - 실행포트 : 7070

