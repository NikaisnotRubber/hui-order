package com.hui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDTO {
    private List<SalesDataPoint> dataPoints;
    private BigDecimal totalRevenue;
    private int totalOrders;
    private LocalDate startDate;
    private LocalDate endDate;
}