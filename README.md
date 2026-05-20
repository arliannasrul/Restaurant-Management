# Restoran Vibe - Katalog & Manajemen Menu Digital

Aplikasi katalog menu restoran modern yang dibangun menggunakan **Jetpack Compose**. Aplikasi ini dirancang untuk memudahkan pengelola restoran dalam memamerkan menu mereka dan mengelola data ketersediaan stok secara digital.

## Fitur Utama

- **Home Screen Interaktif**: Dilengkapi dengan banner promo yang bisa diubah-ubah dan kategori menu cepat.
- **Katalog Menu Lengkap**: Daftar menu yang terbagi berdasarkan kategori (Makanan, Minuman, Camilan) dengan pencarian kategori yang mudah.
- **Manajemen Stok**: Admin dapat melihat dan memperbarui jumlah stok setiap menu secara real-time.
- **Profil Kustom**: Ubah nama restoran, alamat, jam buka, dan upload logo restoran Anda sendiri.
- **Promo Banner Dinamis**: Kelola penawaran spesial langsung dari dalam aplikasi.
- **Multi-Image Slideshow**: Unggah beberapa foto untuk setiap menu guna memberikan tampilan yang lebih menggugah selera.
- **Mode Gelap/Terang**: Dukungan tema gelap yang nyaman di mata dengan palet warna Tosca yang disesuaikan.

## Screenshot Aplikasi

| Home Screen | Daftar Menu | Detail Menu |
|:---:|:---:|:---:|
| ![Home](https://raw.githubusercontent.com/username/repo/main/screenshots/home.png) | ![Menu](https://raw.githubusercontent.com/username/repo/main/screenshots/menu.png) | ![Detail](https://raw.githubusercontent.com/username/repo/main/screenshots/detail.png) |
| *Halaman penyambutan dengan logo, promo, dan kategori.* | *Daftar menu lengkap per kategori dengan indikator stok.* | *Tampilan detail menu dengan slideshow foto dan rating.* |

| Profil Restoran | Edit Menu | Edit Profil |
|:---:|:---:|:---:|
| ![Profil](https://raw.githubusercontent.com/username/repo/main/screenshots/profile.png) | ![Edit Menu](https://raw.githubusercontent.com/username/repo/main/screenshots/edit_menu.png) | ![Edit Profil](https://raw.githubusercontent.com/username/repo/main/screenshots/edit_profile.png) |
| *Informasi lengkap restoran dan pengaturan tema.* | *Formulir lengkap untuk mengubah data menu, harga, stok, dan foto.* | *Pusat kendali identitas restoran dan pengelolaan banner promo.* |

## Penjelasan Teknis

### 1. Arsitektur & Teknologi
- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Image Loading**: Coil untuk pemrosesan gambar URI lokal yang efisien.
- **Navigation**: Jetpack Navigation untuk transisi layar yang mulus.
- **Storage**: SharedPreferences (dengan format JSON) untuk menyimpan data profil dan banner secara permanen.

### 2. UI/UX Modern
- **Theme-Aware**: Seluruh komponen menggunakan warna semantik yang mengikuti mode terang/gelap.
- **Responsive Layout**: Menggunakan `LazyColumn` dan `HorizontalPager` untuk performa daftar yang lancar.
- **Visual Feedback**: Label stok berubah warna (Merah jika habis) untuk memberikan sinyal cepat bagi pengelola.

## Cara Instalasi
1. Clone repository ini.
2. Buka di **Android Studio Iguana** atau versi yang lebih baru.
3. Hubungkan perangkat Android atau gunakan Emulator (Min SDK 24).
4. Klik **Run** atau ketik `./gradlew installDebug` di terminal.

---
*Dibuat untuk tugas Pemrograman Mobile (UTS) - Menu Restoran UTS*
