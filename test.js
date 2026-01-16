import http from 'k6/http';
import { sleep } from 'k6';
import { check } from 'k6';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

/**
 * 테스트 환경 설정
 * - vus : 동시에 몇 명이 요청 보낼지
 * - duration : 몇 초 동안 부하를 줄건지
 */
export const options = {
  vus: 10,
  duration: '30s',
  insecureSkipTLSVerify: true,    // SSL 인증서 신뢰
}


/**
 * 테스트에 필요한 설정과 데이터 준비 단계
 * 테스트 전 한번만 실행된다.
 * 
 * @returns access_token 반환
 */
export function setup() {

  const url = 'https://localhost:8080/login';

  // data
  const payload = JSON.stringify({
    "user_id" : "test",
    "user_pwd" : "test"
  });

  // 헤더
  const params = {
    headers: {
      'Content-Type': 'application/json',
    }
  } 

  // 응답
  const res = http.post(url, payload, params)

  // 결과 확인
  check(res, {
    'status is 200': (r) => r.status === 200
  })

  // 토큰 추출
  const access_token = res.json('access_token');

  // 토큰 확인
  check(access_token, {
    'Token exists': (t) => t !== null && t !== undefined && t !== '',
  })

  return { access_token: access_token };

}

/**
 * 본격적인 테스트 단계로 위의 option 설정에 따라
 * 반복적으로 실행된다.
 * 
 * @param {*} data setup단계에서 반환한 acess_token
 * @returns acess_token이 없을시 테스트 종료
 */
export default function(data) {

  // 토큰
  const access_token = data.access_token;

  if (!access_token) {
    console.error('access_token이 없어 테스트를 건너뜁니다.');
    return;
  }

  const url = "https://localhost:8080/user/info"

  const params = {
    headers: {
      'Authorization': `Bearer ${access_token}`,
    }
  } 

  const res = http.get(url, params);

  check(res, {
    'user info successful': (r) => r.status === 200,
  });

  sleep(1);
}

// html로 결과 출력
export function handleSummary(data) { 
  return { "summary.html": htmlReport(data), }; 
}