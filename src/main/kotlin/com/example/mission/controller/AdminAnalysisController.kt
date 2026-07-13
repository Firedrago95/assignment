package com.example.mission.controller

import com.example.mission.dto.ActivitySummaryResponse
import com.example.mission.service.AdminAnalysisService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/analysis")
class AdminAnalysisController(
    private val adminAnalysisService: AdminAnalysisService
) {

    @GetMapping("/summary")
    fun getActivitySummary(): ActivitySummaryResponse {
        return adminAnalysisService.getActivitySummary()
    }

    @GetMapping("/report")
    fun generateReportCsv(): ResponseEntity<String> {
        val csvContent = adminAnalysisService.generateReportCsv()
        
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily_report.csv")
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")

        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent)
    }
}
