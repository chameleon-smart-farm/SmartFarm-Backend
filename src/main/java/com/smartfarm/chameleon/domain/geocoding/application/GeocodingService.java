package com.smartfarm.chameleon.domain.geocoding.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.geocoding.dto.CoordinateDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeocodingService {

    @Value("${kakao.restapi_key}")
    private String restapi_key;

    public CoordinateDTO convertAddressToCoordinate(String query){

        try {

            // 주소 인코딩
            String encoding_query = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8).replace("+", "%20");

            String kakao_url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + encoding_query;

            // connection 생성
            URL url = new URL(kakao_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Authorization", "KakaoAK " + restapi_key);
            conn.setRequestProperty("Accept", "application/json");

            // 결과값 받아오기
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

            // 종료
            rd.close();
            conn.disconnect();

            // 결과 출력
            String str_result = sb.toString();
            log.info("convertAddressToCoordinate 결과 : " + str_result);

            // json parser를 통해 Object로 변환
            JSONParser jsonParser = new JSONParser();
            Object obj_result = jsonParser.parse(str_result);

            // 카카오 geocoding api는 무조건 jsonObject만 반환
            JSONObject result = (JSONObject) obj_result;

            log.info("Json 객체 변환 : " + result.toString());

            // 응답 json 안에서 documents 배열 가져오기
            JSONArray documents = (JSONArray) result.get("documents");
            // log.info("documents : " + documents);

            // 배열 안의 첫번째 항목(리스트) 가져오기
            JSONObject list = (JSONObject) documents.get(0);
            // log.info("list : " + list);

            // 리스트 안의 address 객체
            JSONObject address = (JSONObject) list.get("address");
            // log.info("address : " + address);

            // x, y 값 추출 : db에 좌표를 int로 저장했기 때문에 int 형으로 변환
            int x = (int) Double.parseDouble(address.get("x").toString()); 
            int y = (int) Double.parseDouble(address.get("y").toString());

            CoordinateDTO coordinateDTO = new CoordinateDTO();
            coordinateDTO.setX(x);
            coordinateDTO.setY(y);

            return coordinateDTO;

        } catch (Exception e) {
            log.error("Geocoding Error - " + e);
            throw new RuntimeException(e);
        }

    }
    
}
