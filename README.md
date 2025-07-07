# Plant_BE
수입·지출 및 예산 관리 서비스 Plant의 백엔드 개발 저장소 입니다.
##  Introduction
###  Developers 
|                            BE                             |                             BE                              |
|:---------------------------------------------------------:|:-----------------------------------------------------------:|
| [<img src="https://avatars.githubusercontent.com/bum0w0" width="100px;" alt="bum0w0"/>](https://github.com/bum0w0)       | [<img src="https://avatars.githubusercontent.com/sanchaehwa" width="100px;" alt="sanchaehwa"/>](https://github.com/sanchaehwa) |
|                       **김진범**                          |                        **양화영**                           |
###  Development Environment
| IDE             | IntelliJ IDEA    |
|-----------------|------------------|
| **Language**    | Java 17          |
| **Framework**   | Spring Boot 3.5.3 |
| **Build Tools** | Gradle 8.14|
| **DataBase**    | MySQL   |

### Project Tech Stack
| **Category** | **Technologies**                                                                                                                                                                                                                                                                                                                                       |
|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Backend**  | ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white) |

### ERD
<img src="https://github.com/user-attachments/assets/eb668c0a-190e-4151-ba9e-7b00dc31c2f9" alt="plant_ERD" width="600"/>



## Cooperation

###  Commit Convention

| Tag       | 설명 |
|-----------|------|
| Feat      | 새로운 기능 추가 |
| Fix       | 버그 수정 |
| Build     | 빌드 관련 파일 수정 |
| Style     | 스타일 수정 |
| Refactor  | 코드 리팩토링  |
| Comment   | 필요한 주석 추가 및 수정 |
| Test      | 테스트 코드 추가, 수정, 삭제  |
| Rename    | 파일, 폴더 이름 수정 |
| Remove    | 파일, 폴더 삭제 |
| Docs      | 문서 추가, 수정, 삭제  |




### Branch Convention

| 브랜치명       | 설명                                 | 예시                          |
|----------------|--------------------------------------|-------------------------------|
| develop        | 통합 개발 브랜치   | develop                       |
| feature/       | 기능 개발 브랜치                       | feature/user-authentication   |
| fix/           | 버그 수정 브랜치                       | fix/invalid-token-handler     |
| refactor/      | 코드 리팩토링  | refactor/jwt-auth-filter      |
| docs/          | 문서 작업 브랜치                       | docs/api-error-codes          |
| test/          | 테스트 코드 작업 브랜치                 | test/user-service-unit        |
