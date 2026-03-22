package com.cybercert.model

import java.util.concurrent.TimeUnit

data class StreakData(val current: Int, val best: Int, val hasStudiedToday: Boolean)

object StreakCalculator {
    fun calculate(sessionDates: List<Long>): StreakData {
        if (sessionDates.isEmpty()) return StreakData(0, 0, false)

        val todayDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        val days = sessionDates.map { TimeUnit.MILLISECONDS.toDays(it) }
            .distinct().sorted()

        val hasStudiedToday = days.last() == todayDay

        // Current streak: consecutive days ending at last study day (today or yesterday)
        val lastDay = days.last()
        val current: Int
        if (lastDay < todayDay - 1) {
            // Last study was 2+ days ago: streak broken
            current = 0
        } else {
            var streak = 1
            for (i in days.size - 2 downTo 0) {
                if (days[i] == days[i + 1] - 1) streak++
                else break
            }
            current = streak
        }

        // Best streak: scan all days for longest consecutive run
        var best = 1
        var run = 1
        for (i in 1 until days.size) {
            if (days[i] == days[i - 1] + 1) {
                run++
                if (run > best) best = run
            } else {
                run = 1
            }
        }

        return StreakData(current, best, hasStudiedToday)
    }
}
