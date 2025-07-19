from locust import HttpUser, SequentialTaskSet, task, between, events
from datetime import datetime, timedelta
import random
import string
import time

MEMBER_HOST = "http://localhost:7070"    # 회원 서버
AUCTION_HOST = "http://localhost:8080"   # 경매 서버

def generate_user_id():
    random_part = ''.join(random.choices(string.ascii_letters + string.digits, k=8))
    timestamp_part = str(int(time.time() * 1000))
    return f"{random_part}_{timestamp_part}"

def generate_password():
    l = random.choice(string.ascii_lowercase)
    u = random.choice(string.ascii_uppercase)
    d = random.choice(string.digits)
    rest = ''.join(random.choices(string.ascii_letters + string.digits, k=9))
    return ''.join(random.sample(l + u + d + rest, 12))

class SellerBehavior(SequentialTaskSet):
    def on_start(self):
        self.user_id = generate_user_id()
        self.password = generate_password()
        self.access_token = None
        self.auction_id = None

        # 회원가입 & 로그인 
        signup = {"signUpId": self.user_id, "password": self.password, "userRole": "SELLER"}
        resp = self.client.post(f"{MEMBER_HOST}/members/signup", json=signup, headers={"Content-Type": "application/json"}, catch_response=True)
        if resp.status_code == 200:
            signin = {"signInId": self.user_id, "password": self.password}
            resp2 = self.client.post(f"{MEMBER_HOST}/members/signin", json=signin, headers={"Content-Type":"application/json"}, catch_response=True)
            if resp2.status_code == 200:
                self.access_token = resp2.json().get("accessToken")
        else:
            print(f"[회원가입 실패] {self.user_id}: {resp.text}")

    def _auth_headers(self):
        return {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }

    @task(1)  # 경매 생성
    def create_auction(self):
        if not self.access_token:
            print("Access token is missing. Cannot create auction.")
            return

        start_time = datetime.now() + timedelta(minutes=1)
        finish_time = start_time + timedelta(minutes=60)
        auction_data = {
            "productName": "Sample Product",
            "originPrice": 10000,
            "stock": 50,
            "maximumPurchaseLimitCount": 5,
            "pricePolicy": {"type": "CONSTANT", "variationWidth": 10},
            "variationDuration": "PT1M",
            "requestTime": datetime.now().isoformat(),
            "startedAt": start_time.isoformat(),
            "finishedAt": finish_time.isoformat(),
            "isShowStock": True
        }
        headers = self._auth_headers()
        with self.client.post(f"{AUCTION_HOST}/auctions", json=auction_data, headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Create auction failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Auction created successfully: {response.text}")
                self.auction_id = response.json().get("id")
                self.get_seller_auctions()  # 다음 단계 호출

    @task(2)  # 경매 목록 조회
    def get_seller_auctions(self):
        if not self.access_token:
            print("Access token is missing. Cannot get seller auctions.")
            return

        headers = self._auth_headers()
        with self.client.get(f"{AUCTION_HOST}/auctions/seller?offset=0&size=10", headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Get seller auctions failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Seller auctions retrieved successfully: {response.text}")
                auctions = response.json()
                if auctions and len(auctions) > 0:
                    auction_id = auctions[0].get("id")
                    self.cancel_auction(auction_id)
                else:
                    print("No auctions found.")

    @task(3)  # 경매 취소
    def cancel_auction(self, auction_id):
        if not self.access_token:
            print("Access token is missing. Cannot cancel auction.")
            return
        if auction_id is None:
            print("No auction ID available for cancellation.")
            return

        headers = self._auth_headers()
        with self.client.delete(f"{AUCTION_HOST}/auctions/{auction_id}", headers=headers, catch_response=True) as response:
            if response.status_code != 200:
                print(f"Cancel auction failed: {response.status_code}, Response: {response.text}")
            else:
                print(f"Auction canceled successfully: {response.text}")

class WebsiteUser(HttpUser):
    host = MEMBER_HOST
    tasks = [SellerBehavior]
    wait_time = between(1, 3)

@events.request.add_listener
def request_handler(request_type, name, response_time, response_length, response, exception, **kwargs):
    if exception:
        print(f"Request to {name} failed with exception")
    else:
        pass
