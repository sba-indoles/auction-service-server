from locust import HttpUser, task, between, SequentialTaskSet, events
from datetime import datetime, timedelta

HOST = "http://localhost:8080"

class SellerBehavior(SequentialTaskSet):
    def on_start(self):
        """테스트 시작 시 필요한 초기 설정"""
        self.access_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZSI6IlNFTExFUiIsImlhdCI6MTczMjg4MjczNCwiZXhwIjoxNzMyOTY5MTM0fQ.FUjW9tInRyAaKWbSiGco_GisvxSj42aXRM0VaEgwLXA"
        self.seller_id = 2
        self.auction_id = None

    @task(1)  # 경매 생성 API 호출
    def create_auction(self):
        if not self.access_token:
            print("Access token is missing. Cannot create auction.")
            return

        # 현재 시간으로부터 1분 후 시작, 60분 후 종료
        start_time = datetime.now() + timedelta(minutes=1)
        finish_time = start_time + timedelta(minutes=60)

        auction_data = {
            "productName": "Sample Product",
            "originPrice": 10000,
            "stock": 50,
            "maximumPurchaseLimitCount": 5,
            "pricePolicy": {
                "type": "CONSTANT",
                "variationWidth": 10
            },
            "variationDuration": "PT1M",
            "requestTime": datetime.now().isoformat(),  # 현재 시간
            "startedAt": start_time.isoformat(),  # 시작 시간
            "finishedAt": finish_time.isoformat(),  # 종료 시간
            "isShowStock": True
        }
        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.post("/auctions", json=auction_data, headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Create auction failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Auction created successfully: {response.text}")
                self.auction_id = response.json().get("id")  # 생성된 경매의 ID를 저장
                self.get_seller_auctions()  # 경매 목록 조회 호출

    @task(2)  # 경매 목록 조회 API 호출
    def get_seller_auctions(self):
        if not self.access_token:
            print("Access token is missing. Cannot get seller auctions.")
            return

        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.get("/auctions/seller?offset=0&size=10", headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Get seller auctions failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Seller auctions retrieved successfully: {response.text}")
                auctions = response.json()  # 전체 경매 목록
                if auctions:  # 경매가 존재하는 경우
                    auction_id = auctions[0].get("id")  # 첫 번째 경매 ID를 가져옴
                    self.cancel_auction(auction_id)  # 경매 취소 호출
                else:
                    print("No auctions found.")

    @task(3)  # 경매 취소 API 호출
    def cancel_auction(self, auction_id):
        if not self.access_token:
            print("Access token is missing. Cannot cancel auction.")
            return

        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }

        # 경매 ID에서 시작 시간을 가져오기 위한 부분
        if self.auction_id is not None:  # 생성된 경매 ID가 있는 경우
            with self.client.delete(f"/auctions/{self.auction_id}", headers=headers, catch_response=True) as response:
                if response.status_code != 200:
                    print(f"Cancel auction failed: {response.status_code}, Response: {response.text}")
                else:
                    print(f"Auction canceled successfully: {response.text}")
        else:
            print("No auction ID available for cancellation.")

class WebsiteUser(HttpUser):
    host = HOST
    tasks = [SellerBehavior]
    wait_time = between(1, 3)

@events.request.add_listener
def request_handler(request_type, name, response_time, response_length, response, exception, **kwargs):
    if exception:
        print(f"Request to {name} failed with exception")
