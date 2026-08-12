# BYD Dolphin Launcher v0.2

Launcher / dashboard open-source cho **BYD DiLink 3** (Android 10), viết mới hoàn toàn (MIT).

## Tính năng

| Tính năng | Chi tiết |
|-----------|----------|
| **Dashboard** | Tốc độ lớn, % pin, range, công suất (kW), số hiện tại |
| **Hoạt ảnh xe** | Silhouette hatchback kiểu Dolphin: đường chạy theo tốc độ, bánh quay, đèn phanh, bounce nhẹ, chỉ báo sạc |
| **TPMS** | 4 bánh (kPa), đổi màu khi thấp/cao |
| **Trip** | Km hành trình, tiêu thụ kWh/100km, thời gian, nhiệt ngoài |
| **App drawer** | Lưới toàn bộ app đã cài, bấm mở |
| **Sáng / tối** | Auto theo hệ thống hoặc luôn tối |
| **Demo mode** | Tốc độ dao động để xem animation khi chưa có data xe |
| **Home launcher** | Có category HOME — set làm màn hình chính được |

## Không phải / giới hạn

- **Không** phải Dudu / Kinex (không copy code thương mại).
- Hoạt ảnh là **2.5D Canvas** (đẹp, mượt, nhẹ), không phải model 3D glTF thật. Có thể thay sau bằng Filament/SceneView.
- Data xe thật cần map key từ content provider trên **firmware của bạn**.

## Build

Yêu cầu: Android Studio + JDK 17.

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Cài lên xe:

- USB: folder `Third Party Apps 55` (hoặc mã nước bạn), password `BYD6125F`
- hoặc `adb connect 192.168.10.10:5555` → `adb install -r app-debug.apk`

## Map data thật

```bash
adb shell "content query --uri content://com.byd.carStatusProvider/car_status"
```

Chỉnh `VehicleRepository.mapProviderKeys()` cho đúng tên cột TPMS / SOC / range.

Tham khảo:

- https://github.com/wheregoes/byd-dolphin-hacking
- https://github.com/wheregoes/byd-apps

## Cấu trúc chính

```
ui/CarAnimation.kt      — hoạt ảnh xe
ui/DashboardScreen.kt   — màn chính
ui/AppDrawerScreen.kt   — danh sách app
ui/SettingsScreen.kt
data/VehicleRepository.kt
MainActivity.kt
```

## Tác giả

**Lê Minh**

## License

MIT. Dùng trên xe là trách nhiệm của bạn.
