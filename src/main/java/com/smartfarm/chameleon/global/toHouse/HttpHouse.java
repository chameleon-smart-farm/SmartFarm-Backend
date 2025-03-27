package com.smartfarm.chameleon.global.toHouse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpHouse {

    /**
     * Get Http Connection
     * 결과값을 JsonObject로 반환
     * 
     * @param get_url
     * @return JsonObject
     */
    public Optional<JSONObject> get_http_connection(String get_url){

        try {
            
            // connection 생성
            URL url = new URL(get_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

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
            log.info("HTTPHouse 결과 : " + str_result);

            // json parser를 통해 JsonObject로 변환
            JSONParser jsonParser = new JSONParser();
            JSONObject result = (JSONObject) jsonParser.parse(str_result);

            log.info("Json 객체 변환 : " + result.toString());

            return Optional.ofNullable(result);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }

    }

    public void put_http_connection(String put_url, Object put_data){

        try {

            // connection 생성
            URL url = new URL(put_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            ObjectMapper objectMapper = new ObjectMapper();
            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                // out.writeBytes(objectMapper.writeValueAsString(put_data));
                out.write(objectMapper.writeValueAsString(put_data).getBytes("UTF-8"));
                out.flush();
            }

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
            log.info("HTTPHouse 결과 : " + str_result);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
    
    
}
