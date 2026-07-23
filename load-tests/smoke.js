import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    steady: {
      executor: "ramping-vus",
      stages: [
        { duration: "20s", target: 10 },
        { duration: "40s", target: 25 },
        { duration: "20s", target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<400", "p(99)<900"],
  },
};

const baseUrl = __ENV.API_BASE_URL || "http://localhost:8080/api/v1";

export function setup() {
  const response = http.post(
    `${baseUrl}/auth/login`,
    JSON.stringify({
      username: __ENV.API_USERNAME,
      password: __ENV.API_PASSWORD,
    }),
    { headers: { "Content-Type": "application/json" } },
  );
  check(response, { "login succeeds": (result) => result.status === 200 });
  return { token: response.json("token") };
}

export default function hospitalApiScenario(data) {
  const response = http.get(`${baseUrl}/patient/search?query=&page=0&size=20`, {
    headers: { Authorization: `Bearer ${data.token}` },
  });
  check(response, {
    "patient search succeeds": (result) => result.status === 200,
    "response is JSON": (result) =>
      (result.headers["Content-Type"] || "").includes("application/json"),
  });
  sleep(1);
}
