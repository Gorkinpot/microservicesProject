package com.example.Service

import com.example.dto.request.BookingCancelRequest
import com.example.dto.request.BookingRequestFromRabbit

object BookingService {
    fun addBookingRequest(request: BookingRequestFromRabbit) {
        BookingRepository.addBookingInfo(request)
    }
    fun deleteBookingRequest(request: BookingCancelRequest) {
        BookingRepository.deleteBookingInfo(request)
    }
}