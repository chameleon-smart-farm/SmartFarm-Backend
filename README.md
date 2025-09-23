**SmartFarm-Backend**

카멜레온 SMART FARM 백엔드 Repo

## **폴더 구조**

- domain : 각 기능별 API 정의
- global : 설정, filter 등 다양한 domain에서 사용되는 기능 정의
- resources : mapper와 logback, application.properties 저장

## **domain**

- house : 농장 서버에 저장되어 있는 농장 정보 조회
- house_machine : 농장의 각종 기기 데이터 상태 조회와 동작 관리
- house_status : 농장 서버에 저장되어 있는 농장의 각종 상태 데이터 반환
- login : 로그인, 로그아웃, 사용자 이름 조회 등 token을 사용한 기능
- reservation : 농장 기기 예약 CRUD
- user : 사용자 정보 CRU와 회원가입 기능

<aside>
❗

농장 기상청 데이터 반환 API house에서 house_status로 이동

기존 사용자의 농장 추가 기능 필요

</aside>

## global

- config : 설정 파일
    - cache, Redis, Security, Swagger, Cors 설정
- filter : token 검사 필터
- jwt : token 발급과 검증, 서비스 파일
- redis : redis 데이터 CRD 파일
- toHouse : 농장 서버 연결 파일



# 핵심 로직 정리

## 회사 서버와 농장 서버

![alt text](/image/image.png)

## 로그인, 로그아웃

[Spring Security + JWT + Redis를 이용한 로그인, 로그아웃 구현](https://www.notion.so/Spring-Security-JWT-Redis-1b5ba818a2ee808db97fd8a6354bafa3?pvs=21) 

## 회원가입

![alt text](/image/image-1.png)
![alt text](/image/image-2.png)
![alt text](/image/image-3.png)

## 농장 서버 연결

- 기상청 API의 코드를 기반으로 작성
- 아래의 예시는 GET Method 기반으로 POST, PUT, DELETE Method와는 처리 방법이 다르다.
    - GET Method에서는 결괏값을 JsonObject로 변환하는 과정이 필요하지만 나머지 Method에서는 확인용으로 String까지만 변환한다.
    - POST, PUT Method에는 Body값을 사용하기 위한 아래의 설정이 필요하다.
        
        ```java
        conn.setDoOutput(true);
        ```
        
    - POST, PUT Method에서는 Body에 데이터를 담기 위해 연결 객체의 OutputStream에 Json Object를 String으로 변환해 담는다.
        
        ```java
        ObjectMapper objectMapper = new ObjectMapper();
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.write(objectMapper.writeValueAsString(put_data).getBytes("UTF-8"));
            out.flush();
        }
        ```
        
- 전체 코드
    
    https://github.com/chameleon-smart-farm/SmartFarm-Backend/blob/main/src/main/java/com/smartfarm/chameleon/global/toHouse/HttpHouse.java
    

### 핵심 로직

1. **connection 생성**
    
    ```java
    // connection 생성
    URL url = new URL(get_url);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-type", "application/json");
    ```
    
2. **connection 결과 받아오기** : 결괏값을 StringBuilder로 변환
    
    ```java
    BufferedReader rd;
    if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    } else {
        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
    }
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
        sb.append(line);
    }
    ```
    connection의 InputStream을 통해 데이터를 가져오며 BufferedReader에 데이터가 있다면 StringBuilder에 데이터를 더한다.

3. **connection 종료**
    
    ```java
    rd.close();
    conn.disconnect();
    ```
    
4. **결과 출력** : 로그 확인용으로 String으로 변환 후 출력
    
    ```java
    String str_result = sb.toString();
    log.info("HTTPHouse 결과 : " + str_result);
    ```
    
5. **JsonObject로 변환** : String 데이터를 Json으로 변환
    
    ```java
    // json parser를 통해 Object로 변환
    JSONParser jsonParser = new JSONParser();
    Object obj_result = jsonParser.parse(str_result);
    ```
    
6. **JsonObject 또는 JsonArray 변환** : 실제 사용을 위한 형태 변환
    
    ```java
    // 반환값이 JsonObject인지 JsonArray형태인지 확인 후 각각 변환
    if (obj_result instanceof JSONObject) {
        
        JSONObject result = (JSONObject) obj_result;
    
        log.info("Json 객체 변환 : " + result.toString());
    
        return Optional.ofNullable(result);
    } else {
    
        JSONArray result = (JSONArray) obj_result;
    
        log.info("Json 리스트 변환 : " + result.toString());
    
        return Optional.ofNullable(result);
    }
    ```
    

## 농장 기기 제어

- 농장 서버 기기 상태 조회 - GET Method
    
    ```java
    // 농장 서버에 모터 상태를 조회 요청을 보내는 메서드
    public HouseMachineDTO get_motor_status(int house_id){
    
        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/status";
    
        // get 요청
        JSONObject res_result = (JSONObject) httpHouse.get_http_connection(get_url).get();
    
        // 결과 생성
        HouseMachineDTO result = new HouseMachineDTO();
        result.setMotor_status(Boolean.parseBoolean(res_result.get("motor_status").toString()));
    
        // 결과 출력
        log.info("get_motor_status - 모터 상태 : " + result.isMotor_status());
    
        return result;
    }
    ```
    
    - JsonObject에 저장되어 있는 motor_status를 HouseMachineDTO에 저장
    - **motor_status값은 true, false값으로 전달**된다.
        
        → 변경될 수 있음
        

- 농장 서버 기기 제어 - POST Method
    
    ```java
    // PLC에 모터 on/off 요청을 보내는 메서드
    public void motor_on_off(int house_id, HouseMachineDTO status){
    
        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String post_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/on_off";
    
        // post 요청
        httpHouse.post_http_connection(post_url, status);
    
    }
    ```
    
    - **HouseMachineDTO안의 motor_status값은 true, false값으로 전달**한다.
        
        → 변경될 수 있음