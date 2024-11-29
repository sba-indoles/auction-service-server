from locust import HttpUser, task, between, SequentialTaskSet, events
from datetime import datetime, timedelta

HOST = "http://localhost:8080"

class SellerBehavior(SequentialTaskSet):
    def on_start(self):
        """테스트 시작 시 필요한 초기 설정"""
        #Seller의 액세스 토큰
        self.access_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZSI6IlNFTExFUiIsImlhdCI6MTczMjg4MjczNCwiZXhwIjoxNzMyOTY5MTM0fQ.FUjW9tInRyAaKWbSiGco_GisvxSj42aXRM0VaEgwLXA"
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
            "requestTime": datetime.now().isoformat(),
            "startedAt": start_time.isoformat(),
            "finishedAt": finish_time.isoformat(),
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
                try:
                    if response.text:
                        self.auction_id = response.json().get("id")
                        if not self.auction_id:
                            print("No auction ID found in the response.")
                        else:
                            self.buyer_test()  # Buyer 테스트 시작
                    else:
                        print("Received empty response.")
                except ValueError as e:
                    print(f"Failed to parse JSON response: {e}")
                    print(f"Response content: {response.text}")

    def buyer_test(self):
        """Buyer 테스트 실행"""
        buyer = BuyerBehavior(self.client)
        # Buyer의 액세스 토큰
        buyer.access_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkJVWUVSIiwiaWF0IjoxNzMyODg1OTg2LCJleHAiOjE3MzI5NzIzODZ9.PnuOC8tHfDjTWUsKy1a-vIUTbEdRrHfnC3ti5WFhpZ4"
        buyer.auction_id = self.auction_id  # Seller가 생성한 경매 ID를 Buyer에 전달
        buyer.submit_bid()  # 입찰 요청 호출

class BuyerBehavior(HttpUser):  # HttpUser 상속
    def on_start(self):
        self.receipt_id = None  # 영수증 ID를 저장할 변수
        self.access_token = None  # 액세스 토큰 초기화
        self.auction_id = None  # 경매 ID 초기화

    @task
    def submit_bid(self):
        if not self.access_token or self.auction_id is None:
            print("Access token or auction ID is missing. Cannot submit bid.")
            return

        bid_amount = 10000  # 지정된 가격
        quantity = 1  # 지정된 수량

        purchase_request = {
            "price": bid_amount,
            "quantity": quantity
        }
        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.post(f"/auctions/{self.auction_id}/purchase", json=purchase_request, headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Bid submission failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Bid submitted successfully: {response.text}")
                self.receipt_id = response.json().get("receiptId")  # 영수증 ID 저장

                if not self.receipt_id:
                    print("No receipt ID found in the response. Cannot proceed with cancellation.")
                else:
                    self.cancel_bid(self.receipt_id)  # 입찰 취소 요청 호출

    def cancel_bid(self, receipt_id):
        if not self.access_token or receipt_id is None:
            print("Access token or receipt ID is missing. Cannot cancel bid.")
            return

        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }
        with self.client.delete(f"/auctions/{receipt_id}/refund", headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Cancel bid failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Bid canceled successfully: {response.text}")

class WebsiteUser(HttpUser):
    host = HOST
    tasks = [SellerBehavior]
    wait_time = between(1, 3)

@events.request.add_listener
def request_handler(request_type, name, response_time, response_length, response, exception, **kwargs):
    if exception:
        print(f"Request to {name} failed with exception: {exception}")
    else:
        print(f"Successfully made a request to: {name} with response time: {response_time}ms")
