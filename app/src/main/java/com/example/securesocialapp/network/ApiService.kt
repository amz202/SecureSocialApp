package com.example.securesocialapp.network
import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.data.model.ActivityLog
import com.example.securesocialapp.data.model.request.LoginRequest
import com.example.securesocialapp.data.model.request.OtpRequest
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.model.request.RefreshRequest
import com.example.securesocialapp.data.model.request.RegisterRequest
import com.example.securesocialapp.data.model.response.TokenPair
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    // Auth endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ResponseBody

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): TokenPair

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): TokenPair

    @GET("auth/check-username")
    suspend fun checkUsername(@Query("username") username: String): Map<String, Boolean>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): String

    @POST("auth/resend-otp")
    suspend fun resendOtp(@Query("email") email: String): String

    // Post endpoints
    @POST("api/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: PostRequest
    ): PostResponse

    @GET("api/posts")
    suspend fun getAllPosts(@Header("Authorization") token: String): List<PostResponse>

    @GET("api/posts/{postId}")
    suspend fun getPost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): PostResponse

    @GET("api/posts/tag/{tagName}")
    suspend fun getPostsByTag(@Path("tagName") tagName: String): List<PostResponse>

    @POST("api/posts/{postId}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): ResponseBody

    // Dashboard endpoints
    @GET("activity-log")
    suspend fun getActivityLog(@Header("Authorization") token: String): List<ActivityLog>

    // Demo/Tamper endpoints
    @POST("api/demo/corrupt-like/{likeId}")
    suspend fun corruptLike(@Path("likeId") likeId: String): String

    @GET("api/demo/verify-like/{likeId}")
    suspend fun verifyLike(@Path("likeId") likeId: String): Map<String, Any>
}
