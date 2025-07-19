from locust import HttpUser, SequentialTaskSet, task, between, events
from datetime import datetime, timedelta
import random, string, time

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

class BuyerFlow(SequentialTaskSet):
    def on_start(self):
        # --- 판매자(Seller) 회원가입/로그인 ---
        self.seller_id = generate_user_id()
        self.seller_pw = generate_password()
        self.seller_token = None
        self.buyer_id = generate_user_id()
        self.buyer_pw = generate_password()
        self.buyer_token = None
        self.auction_id = None
        self.receipt_id = None
        # Seller 회원가입/로그인
        signup_seller = {"signUpId": self.seller_id, "password": self.seller_pw, "userRole": "SELLER"}
        resp1 = self.client.post(f"{MEMBER_HOST}/members/signup", json=signup_seller, headers={"Content-Type": "application/json"}, catch_response=True)
        if resp1.status_code == 200:
            signin = {"signInId": self.seller_id, "password": self.seller_pw}
            resp2 = self.client.post(f"{MEMBER_HOST}/members/signin", json=signin, headers={"Content-Type":"application/json"}, catch_response=True)
            if resp2.status_code == 200:
                self.seller_token = resp2.json().get("accessToken")
        # --- 판매자 경매 생성 ---
        if self.seller_token:
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
            seller_headers = {
                "Authorization": f"Bearer {self.seller_token}",
                "Content-Type": "application/json"
            }
            res = self.client.post(f"{AUCTION_HOST}/auctions", json=auction_data, headers=seller_headers, catch_response=True)
            if res.status_code == 200:
                self.auction_id = res.json().get("id")
        # --- 구매자(Buyer) 회원가입/로그인 ---
        signup_buyer = {"signUpId": self.buyer_id, "password": self.buyer_pw, "userRole": "BUYER"}
        resp3 = self.client.post(f"{MEMBER_HOST}/members/signup", json=signup_buyer, headers={"Content-Type": "application/json"}, catch_response=True)
        if resp3.status_code == 200:
            signin = {"signInId": self.buyer_id, "password": self.buyer_pw}
            resp4 = self.client.post(f"{MEMBER_HOST}/members/signin", json=signin, headers={"Content-Type":"application/json"}, catch_response=True)
            if resp4.status_code == 200:
                self.buyer_token = resp4.json().get("accessToken")

    @task
    def bid_and_cancel(self):
        # --- Buyer가 Seller의 auction_id에 입찰 ---
        if not self.buyer_token or not self.auction_id:
            print("Missing token or auction_id. Cannot bid.")
            return
        bid_data = {
            "price": 10000,
            "quantity": 1
        }
        buyer_headers = {
            "Authorization": f"Bearer {self.buyer_token}",
            "Content-Type": "application/json"
        }
        # 입찰
        with self.client.post(f"{AUCTION_HOST}/auctions/{self.auction_id}/purchase", json=bid_data, headers=buyer_headers, catch_response=True) as res:
            if res.status_code == 200:
                print(f"[입찰 성공] auction_id={self.auction_id}, buyer={self.buyer_id}")
                self.receipt_id = res.json().get("receiptId")
                if self.receipt_id:
                    self.cancel_bid(self.receipt_id)
                else:
                    print("No receiptId in response; cannot cancel bid.")
            else:
                print(f"[입찰 실패] {res.status_code} {res.text}")

    def cancel_bid(self, receipt_id):
        if not self.buyer_token or receipt_id is None:
            print("Token or receipt ID missing, cannot cancel bid.")
            return
        buyer_headers = {
            "Authorization": f"Bearer {self.buyer_token}",
            "Content-Type": "application/json"
        }
        with self.client.delete(f"{AUCTION_HOST}/auctions/{receipt_id}/refund", headers=buyer_headers, catch_response=True) as res:
            if res.status_code == 200:
                print(f"[입찰 취소 성공] receipt_id={receipt_id}")
            else:
                print(f"[입찰 취소 실패] status_code={res.status_code}, response={res.text}")

class WebsiteUser(HttpUser):
    host = MEMBER_HOST
    tasks = [BuyerFlow]
    wait_time = between(1, 3)

@events.request.add_listener
def request_handler(request_type, name, response_time, response_length, response, exception, **kwargs):
    if exception:
        print(f"Request to {name} failed with exception")
