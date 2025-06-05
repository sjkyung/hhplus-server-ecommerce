import http from 'k6/http';
import { check } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    scenarios: {
        spike_then_cooldown: {
            executor: 'ramping-arrival-rate',
            startRate: 0,
            timeUnit: '1s',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
            stages: [
                {target: 1000, duration: '10s'},
                {target: 5000, duration: '10s'},
                {target: 30, duration: '37s'},
                {target: 3, duration: '10s'},
            ],
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)< 500'],
    },
};

export default function () {
    const couponId = randomIntBetween(1, 4);
    const url = `http://host.docker.internal:8080/api/v1/coupons/${couponId}/issue`;
    const payload = JSON.stringify({
        userId: randomIntBetween(1, 5000),
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        '200 응답 확인': (r) => r.status === 200,
    });

    sleep(1);
}