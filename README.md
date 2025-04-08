# BazaarIO 🛒

<img align="left" src="https://github.com/user-attachments/assets/4cc64283-5eeb-4fa0-a04c-eedbf7d212e3" alt="BazaarIO Logo" width="200"/>

- [🚀 Features](#-features)  
- [🧱 Tech Stack & Architecture](#-tech-stack--architecture)  
- [📸 Screenshots](#-screenshots)  
- [📦 Installation & Setup](#-installation--setup)  
- [🧪 Demo](#-demo)  
- [📄 License](#-license)  
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

<b>Tech Stack</b>

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
<tr><td><b>Stepper</b></td><td>Kotstep</td></tr>
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
                ├── data/                    # Data layer
                │   ├── di/                  # Hilt modules
                │   └── repositoryImpl/
                ├── domain/                  # Use cases and models
                │   ├── model/
                │   └── usecase/
                ├── presentation/            # UI layer
                │   ├── navigation/          # Navigation graphs
                │   ├── screen/              # Composables by screen
                │   │   ├── home/
                │   │   ├── product/
                │   │   ├── cart/
                │   │   ├── auth/
                │   │   └── admin/
                │   └── components/          # Shared composables
                └── util/                    # Utils, constants
</pre>

</td>
</tr>
</table>
