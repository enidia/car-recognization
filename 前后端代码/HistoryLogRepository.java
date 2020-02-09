package com.example.demo.service;

import com.example.demo.bean.HistoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryLogRepository extends JpaRepository<HistoryLog, Long> {
}
