# SecureSocial

A security-focused social media application built to demonstrate robust authentication flows. It combines strict security practices, like OTP verification and automatic token management, with a polished, responsive Material 3 user interface.

---

## Features

- **Email-based registration** with OTP verification  
- **JWT authentication** with automatic access-token refresh  
- **Password rules** and server-side username availability checks  
- **Session persistence** using Jetpack DataStore  

### Social Features

- Feed with tag-based filtering and relative timestamps  
- Post creation with text input and tag selection  
- Like posts and view the list of users who liked a post
- Comment on the posts, with timestamps
- Activity log for login, posts, comments and likes  

### Security (Backend-Enforced)

- **HMAC-signed likes**  
- **Hashed user identifiers** for incognito views  

---

## Tech Stack

- **Language:** Kotlin  
- **UI Framework:** Jetpack Compose (Material 3)  
- **Architecture:** MVVM with state flow  
- **Networking:** Retrofit + OkHttp (interceptors & authenticators)  
- **Local Storage:** Jetpack DataStore  
- **Serialization:** Gson / kotlinx.serialization  
- **Backend:** [SpringBoot Backend](https://github.com/amz202/spring-SecureSocial) Spring Boot + MongoDB  

---

## Project Structure Highlights

- Clean separation across UI, data, and domain layers  
- Repository pattern for data access  
- Centralized authentication through a custom OkHttp Authenticator  
- ViewModels as the single source of truth for UI state  
- Navigation flow avoiding race conditions during logout  
- Reusable Compose components for posts, dialogs, and tag selectors  

---

## UI Previews

### Authentication
<img width="1560" height="1080" alt="1" src="https://github.com/user-attachments/assets/932e97bc-f7d3-4a9f-b03c-88923e943051" />

### Posts, Activity Log, My Posts
<img width="1560" height="1080" alt="2" src="https://github.com/user-attachments/assets/15d28fb7-39a0-4143-9495-006b1c355a0f" />

### Add Post, Post Info
<img width="1600" height="900" alt="3" src="https://github.com/user-attachments/assets/099c4b83-ac23-4a5e-9992-caaebc3bf582" />

---

## License

This project is licensed under the [MIT License](./LICENSE).
