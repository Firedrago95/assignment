package com.example.mission.global.exception

class ExternalApiException(
    errorCode: ErrorCode,
    message: String? = errorCode.message
) : BusinessException(errorCode, message)
