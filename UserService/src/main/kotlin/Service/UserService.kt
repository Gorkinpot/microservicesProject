package com.example.Service

import com.example.dto.request.RegisterRequest

class UserService(
    private val repo: UserRepository,
) {

    fun register(req: RegisterRequest) {
        repo.registerUser(req)
    }
}
