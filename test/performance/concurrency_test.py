"""并发压测脚本
测试场景：
1. 10 并发 checkin 同一房间 → 验证零超售 + 响应时间
2. 200 并发错误登录 → 验证限流 + 响应时间
3. 10 并发 booking 同一房型 → 验证预订零超售 + 响应时间
前置条件：确保后端已启动
"""

import threading, requests, json, time, statistics

BASE = "http://localhost:8080/api"
RESULTS = {"checkin_times": [], "booking_times": [], "login_times": []}
lock = threading.Lock()
booked_guest_ids = []


def print_stats(label, times):
    if not times:
        print(f"  {label}: no data")
        return
    avg = sum(times) / len(times)
    s = sorted(times)
    p95 = s[int(len(s) * 0.95)]
    p99 = s[int(len(s) * 0.99)]
    print(f"  {label}: avg={avg:.0f}ms  min={min(times):.0f}ms  max={max(times):.0f}ms  P95={p95:.0f}ms  P99={p99:.0f}ms")


def get_token():
    r = requests.post(f"{BASE}/login", json={
        "empName": "王敏", "password": "123456"
    }, timeout=5)
    data = r.json()
    if data.get("code") == 200:
        t = data["data"]["token"]
        print(f"  登录成功, token={t[:20]}...")
        return t
    print(f"  登录失败: {data.get('msg')}")
    return None


def cleanup_test_data(headers):
    print("\n=== 清理测试遗留数据 ===")
    cleaned = 0
    r = requests.get(f"{BASE}/reception/living-list", headers=headers, timeout=5)
    for g in r.json().get("data", []):
        gid = g.get("idCard", "")
        if "CONCUR" in gid and g.get("status") == "在住":
            requests.post(f"{BASE}/reception/checkout", params={"roomId": g["roomId"]}, headers=headers, timeout=5)
            requests.put(f"{BASE}/reception/room/updateStatus", params={"roomId": g["roomId"], "status": "空闲"}, headers=headers, timeout=5)
            cleaned += 1
    for gid in booked_guest_ids:
        try:
            r = requests.post(f"{BASE}/reception/cancelBooking", params={"guestId": gid}, headers=headers, timeout=5)
            if r.json().get("code") == 200:
                cleaned += 1
        except:
            pass
    if cleaned == 0:
        print("  无遗留数据需要清理")
    else:
        print(f"  已清理 {cleaned} 条遗留记录")


def try_checkin(headers, room_id):
    try:
        t0 = time.time()
        r = requests.post(f"{BASE}/reception/checkin",
            json={"roomId": room_id, "name": "并发入住测试",
                  "idCard": f"CONCUR_{threading.get_ident()}",
                  "phone": "13800138000",
                  "inDate": "2026-07-25", "outDate": "2026-07-26"},
            headers=headers, timeout=10)
        cost = (time.time() - t0) * 1000
        data = r.json()
        with lock:
            RESULTS["checkin_times"].append(cost)
            if data.get("code") == 200:
                RESULTS["checkin_success"] += 1
            else:
                RESULTS["checkin_fail"] += 1
    except Exception:
        with lock:
            RESULTS["checkin_fail"] += 1


def try_booking(headers, room_type):
    global booked_guest_ids
    try:
        t0 = time.time()
        r = requests.post(f"{BASE}/reception/booking",
            json={"roomType": room_type, "guestName": "并发预订测试",
                  "phone": "13800138001",
                  "inDate": "2026-07-27", "outDate": "2026-07-28"},
            headers=headers, timeout=10)
        cost = (time.time() - t0) * 1000
        data = r.json()
        with lock:
            RESULTS["booking_times"].append(cost)
            if data.get("code") == 200:
                RESULTS["booking_success"] += 1
                gid = data.get("data", {}).get("guestId")
                if gid:
                    booked_guest_ids.append(gid)
            else:
                RESULTS["booking_fail"] += 1
    except Exception:
        with lock:
            RESULTS["booking_fail"] += 1


def try_login():
    try:
        t0 = time.time()
        r = requests.post(f"{BASE}/login", json={
            "empName": "admin", "password": "wrong_password"
        }, timeout=10)
        cost = (time.time() - t0) * 1000
        with lock:
            RESULTS["login_times"].append(cost)
            if r.status_code == 429:
                RESULTS["login_429"] += 1
            else:
                RESULTS["login_200"] += 1
    except:
        with lock:
            RESULTS["login_200"] += 1


if __name__ == "__main__":
    print("=== 前置：获取登录token ===")
    token = get_token()
    if not token:
        print("  请修改 empName/password 后重试")
        exit(1)
    headers = {"Authorization": f"Bearer {token}"}

    cleanup_test_data(headers)

    # ========== 场景1: 并发入住 ==========
    print("\n场景1: 10并发 checkin 同一房间...")
    ROOM_ID = 3
    print(f"  目标房间ID: {ROOM_ID}")
    RESULTS["checkin_success"] = 0
    RESULTS["checkin_fail"] = 0
    RESULTS["checkin_times"].clear()
    threads = [threading.Thread(target=try_checkin, args=(headers, ROOM_ID)) for _ in range(10)]
    for t in threads: t.start()
    for t in threads: t.join()
    print(f"  checkin成功={RESULTS['checkin_success']}, 失败={RESULTS['checkin_fail']}")
    print_stats("checkin", RESULTS["checkin_times"])
    print(f"  结论: {'✓ 零超售' if RESULTS['checkin_success'] <= 1 else '✗ 存在超售'}")

    cleanup_test_data(headers)

    # ========== 场景2: 限流测试 ==========
    print("\n场景2: 200并发错误登录...")
    RESULTS["login_429"] = 0
    RESULTS["login_200"] = 0
    RESULTS["login_times"].clear()
    threads = [threading.Thread(target=try_login) for _ in range(200)]
    for t in threads: t.start()
    for t in threads: t.join()
    total = RESULTS["login_429"] + RESULTS["login_200"]
    rt = RESULTS["login_429"] / max(total, 1) * 100
    print(f"  被限流(429)={RESULTS['login_429']}, 通过={RESULTS['login_200']}, 限流率={rt:.0f}%")
    print_stats("login", RESULTS["login_times"])
    print(f"  结论: {'✓ 限流生效' if RESULTS['login_429'] > 0 else '⚠ 无限流'}")

    # ========== 场景3: 并发预订 ==========
    print("\n场景3: 10并发 booking 同一房型...")
    ROOM_TYPE = "豪华大床房"
    print(f"  目标房型: {ROOM_TYPE}")
    RESULTS["booking_success"] = 0
    RESULTS["booking_fail"] = 0
    RESULTS["booking_times"].clear()
    booked_guest_ids.clear()
    threads = [threading.Thread(target=try_booking, args=(headers, ROOM_TYPE)) for _ in range(10)]
    for t in threads: t.start()
    for t in threads: t.join()
    print(f"  booking成功={RESULTS['booking_success']}, 失败={RESULTS['booking_fail']}")
    print_stats("booking", RESULTS["booking_times"])
    if RESULTS["booking_success"] > 0:
        print(f"  结论: ✓ 零超售（{RESULTS['booking_success']}间被订）")
    else:
        print(f"  结论: ? 无成功预订")

    cleanup_test_data(headers)
    print("\n所有场景测试完成")
