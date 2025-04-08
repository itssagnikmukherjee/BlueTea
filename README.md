# BazaarIO 🛒

<img align="left" src="https://github.com/user-attachments/assets/4cc64283-5eeb-4fa0-a04c-eedbf7d212e3" alt="BazaarIO Logo" width="200"/>

- [🚀 Features](#-features)  
- [🧱 Tech Stack & Architecture](#-tech-stack--architecture)  
- [📸 Screenshots](#-screenshots)  
- [📦 Installation & Setup](#-installation--setup) 
- [📄 Credits](#-license)  
- [📬 Contact](#-contact)

<br><br>

> **Want to build your own online store and scale your business?**  
> **BazaarIO** is the all-in-one **e-commerce builder app** that empowers individuals to create their own custom shopping platforms — complete with product management, order tracking, customer authentication, delivery integration, and more.


## 🚀 Features

![image](https://github.com/user-attachments/assets/db4b810c-7949-4cfb-9fe4-85fb90cb8321)

- 🖼️ Image upload and handling via [Supabase Storage](https://supabase.com/storage)
- 🔥 Real-time product & user data using [Firebase Realtime Database](https://firebase.google.com/docs/database)
- 🔐 Secure login/register with [Firebase Authentication](https://firebase.google.com/docs/auth)
- 💳 Seamless checkout via [Stripe Android SDK](https://docs.stripe.com/sdks/android)
- 🧾 Order placement & tracking
- 🛒 Wishlist & cart system
- 📦 Admin dashboard for product & order management
- 📲 Modern Jetpack Compose UI


## 🧱 Tech Stack & Architecture

<table>
<tr>
<td valign="top" width="50%">

<b>Tech Stack & Libraries</b>

<table>
<tr><td><b>Language</b></td><td>Kotlin</td></tr>
<tr><td><b>UI</b></td><td>Jetpack Compose</td></tr>
<tr><td><b>Architecture</b></td><td>MVVM + Clean Architecture</td></tr>
<tr><td><b>DI</b></td><td>Hilt</td></tr>
<tr><td><b>Networking</b></td><td>Retrofit</td></tr>
<tr><td><b>Authentication</b></td><td>Firebase Auth</td></tr>
<tr><td><b>Database</b></td><td>Firebase Realtime DB</td></tr>
<tr><td><b>Storage</b></td><td>Supabase Storage</td></tr>
<tr><td><b>Payments</b></td><td>Stripe</td></tr>
<tr><td><b>Image Loading</b></td><td>Coil</td></tr>
<tr><td><b>Stepper</b></td><td><a href="https://github.com/binayshaw7777/KotStep">Kotstep by BinayShaw7777</a></td></tr>
</table>

</td>

<td valign="top" width="50%">

<b>Project Structure</b>

<pre>
BazaarIO/
└── app/
    └── src/
        └── main/
            └── java/com/yourname/bazaario/                   
                ├── data/            # Data layer
                │   ├── di/          # Hilt modules
                │   └── repositoryImpl/
                ├── domain/          # Use cases and models
                │   ├── model/
                │   └── usecase/
                ├── presentation/    # UI layer
                │   ├── navigation/  # Navigation graphs
                │   ├── screen/      # Composables by screen

                │   │   ├── home/
                │   │   ├── product/
                │   │   ├── cart/
                │   │   ├── auth/
                │   │   └── admin/
                │   └── components/  # Shared composables
                └── util/            # Utils, constants
</pre>

</td>
</tr>
</table>

## 📸 Screenshots

### Admin Side
| Splash Screen | ShimmerScreen | Admin Sidebar | Order Management |
|---------------|----------------|----------------|-------------------|
| <img src="https://github.com/user-attachments/assets/7e1d02b7-0609-4345-b0a9-5ade1f2ce54b" width="200"/> | <img src="https://github.com/user-attachments/assets/fce7b0e5-9e54-47e6-bd19-49c1e8f7e3d8" width="200"/> | <img src="https://github.com/user-attachments/assets/4a8892dc-6437-4656-bbba-f1ad27611e11" width="200"/> | <img src="https://github.com/user-attachments/assets/7b9ac976-d5db-4351-9e8c-1e513ffcbe55" width="200"/> |

| Banner Settings | Categories | Banners Section | Product Management |
|-----------------|------------|------------------|---------------------|
| <img src="https://github.com/user-attachments/assets/ea5490eb-22be-4559-91f4-980fbc7e4dc0" width="200"/> | <img src="https://github.com/user-attachments/assets/af8e0839-55d1-407a-ba98-9b132482ace2" width="200"/> | <img src="https://github.com/user-attachments/assets/f94c5e1b-8e86-48d8-a88e-0b11911125cf" width="200"/> | <img src="https://github.com/user-attachments/assets/18795f96-34f9-4b6b-87eb-5f8f47dafa28" width="200"/> |

### User Side
| Home | Favorites | Cart | User Profile |
|------|-----------|------|---------------|
| <img src="https://github.com/user-attachments/assets/075f81c2-57be-46db-923c-ddcfc794512a" width="200"/> | <img src="https://github.com/user-attachments/assets/a0c5e5ae-f1b2-4664-8afd-09f1ce12dd76" width="200"/> | <img src="https://github.com/user-attachments/assets/5de786a8-bb33-455d-b748-2e0831f77c35" width="200"/> | <img src="https://github.com/user-attachments/assets/99bcd067-51ba-45c3-b63f-b8b4de32451f" width="200"/> |

| Product Details | Payment | Orders | Order Tracking |
|------------------|---------|--------|-----------------|
| <img src="https://github.com/user-attachments/assets/f768c4d1-698b-4057-b114-b043345b8de8" width="200"/> | <img src="https://github.com/user-attachments/assets/08ee19f4-1838-4bd1-b1db-b64b72b1bf6a" width="200"/> | <img src="https://github.com/user-attachments/assets/89af547e-a030-4db2-87f3-3516b9d15f64" width="200"/> | <img src="https://github.com/user-attachments/assets/9853499a-be64-4878-9eff-5b5cd5ecff2c" width="200"/> |

| Multiple Checkout | Checkout | Categories | Alert Box |
|-------------------|----------|------------|------------|
| <img src="https://github.com/user-attachments/assets/4d1135b0-3a5e-42c4-bc6a-de426f5965be" width="200"/> | <img src="https://github.com/user-attachments/assets/567f7a1e-da0c-4604-b3bc-975b11668252" width="200"/> | <img src="https://github.com/user-attachments/assets/a5015d1d-3a3b-4382-b4ce-ceb249e4e0ed" width="200"/> | <img src="https://github.com/user-attachments/assets/d7fb6ce4-991d-461c-b715-42d179f8561f" width="200"/> |
