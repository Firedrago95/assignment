package com.example.mission.domain.chat

enum class ChatStatus {
    COMPLETED, // 정상 저장 완료
    PENDING,   // AI 응답 대기 중 또는 스트리밍 중
    FAILED     // API 오류 등으로 실패
}
