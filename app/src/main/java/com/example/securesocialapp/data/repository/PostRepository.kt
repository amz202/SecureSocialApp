package com.example.securesocialapp.data.repository

import com.example.securesocial.data.model.response.PostResponse
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.network.ApiService
import okhttp3.ResponseBody

interface PostRepository{
    suspend fun createPost(request: PostRequest): PostResponse
    suspend fun getAllPosts(): List<PostResponse>
    suspend fun getPost(postId: String): PostResponse
    suspend fun getPostsByTag(tagName: String): List<PostResponse>
    suspend fun likePost(postId: String): ResponseBody
    suspend fun getMyPosts(): List<PostResponse>
}

class PostRepositoryImpl(
    private val apiService: ApiService
): PostRepository{
    override suspend fun createPost(request: PostRequest): PostResponse {
        return apiService.createPost(request)
    }

    override suspend fun getAllPosts(): List<PostResponse> {
        return apiService.getAllPosts()
    }

    override suspend fun getPost(postId: String): PostResponse {
        return apiService.getPost(postId)
    }

    override suspend fun getPostsByTag(tagName: String): List<PostResponse> {
        return apiService.getPostsByTag(tagName)
    }

    override suspend fun likePost(postId: String): ResponseBody {
        return apiService.likePost(postId)
    }

    override suspend fun getMyPosts(): List<PostResponse> {
        return apiService.getMyPosts()
    }
}