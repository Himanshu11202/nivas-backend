package com.society.repository;

import com.society.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all expenses ordered by date desc
    List<Expense> findAllByOrderByDateDesc();

    // Find expenses by month and year
    @Query("SELECT e FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year ORDER BY e.date DESC")
    List<Expense> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Calculate total expense for a month
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year")
    BigDecimal getTotalExpenseByMonth(@Param("month") int month, @Param("year") int year);

    // Get expense breakdown by type for a month
    @Query("SELECT e.type, SUM(e.amount) FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year GROUP BY e.type")
    List<Object[]> getExpenseBreakdownByType(@Param("month") int month, @Param("year") int year);

    // Get monthly totals for the last 6 months
    @Query("SELECT YEAR(e.date), MONTH(e.date), SUM(e.amount) FROM Expense e WHERE e.date >= :startDate GROUP BY YEAR(e.date), MONTH(e.date) ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC")
    List<Object[]> getMonthlyTotals(@Param("startDate") LocalDate startDate);

    // Find expenses between dates
    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);
}
