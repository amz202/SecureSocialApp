package com.example.securesocialapp.data.repository

import com.example.securesocial.data.model.response.PostLikesResponse
import com.example.securesocialapp.data.model.request.PostCommentRequest
import com.example.securesocialapp.data.model.response.PostResponse
import com.example.securesocialapp.data.model.request.PostRequest
import com.example.securesocialapp.data.model.response.PostCommentResponse
import com.example.securesocialapp.data.model.response.PostListResponse
import com.example.securesocialapp.network.ApiService
import okhttp3.ResponseBody

interface PostRepository{
    suspend fun createPost(request: PostRequest): PostResponse
    suspend fun getAllPosts(): List<PostListResponse>
    suspend fun getPost(postId: String): PostResponse
    suspend fun getPostsByTag(tagName: String): List<PostListResponse>
    suspend fun likePost(postId: String): ResponseBody
    suspend fun getMyPosts(): List<PostListResponse>
    suspend fun getPostLikes(postId: String): List<PostLikesResponse>
    suspend fun unlikePost(postId: String): ResponseBody
    suspend fun getPostComments(postId: String): List<PostCommentResponse>
    suspend fun createComment(postId: String, request: PostCommentRequest): PostCommentResponse
    suspend fun deleteComment(postId:String, commentId: String): ResponseBody
}

class PostRepositoryImpl(
    private val apiService: ApiService
): PostRepository{
    override suspend fun createPost(request: PostRequest): PostResponse {
        return apiService.createPost(request)
    }

    override suspend fun getAllPosts(): List<PostListResponse> {
        return apiService.getAllPosts()
    }

    override suspend fun getPost(postId: String): PostResponse {
        return apiService.getPost(postId)
    }

    override suspend fun getPostsByTag(tagName: String): List<PostListResponse> {
        return apiService.getPostsByTag(tagName)
    }

    override suspend fun likePost(postId: String): ResponseBody {
        return apiService.likePost(postId)
    }

    override suspend fun getMyPosts(): List<PostListResponse> {
        return apiService.getMyPosts()
    }

    override suspend fun getPostLikes(postId: String): List<PostLikesResponse> {
        return apiService.getPostLikes(postId)
    }

    override suspend fun unlikePost(postId: String): ResponseBody {
        return apiService.unlikePost(postId)
    }

    override suspend fun getPostComments(postId: String): List<PostCommentResponse> {
        return apiService.getPostComments(postId)
    }

    override suspend fun createComment(postId: String, request: PostCommentRequest): PostCommentResponse {
        return apiService.createComment(postId, request)
    }

    override suspend fun deleteComment(postId: String, commentId: String): ResponseBody {
        return apiService.deleteComment(postId, commentId)
    }
}