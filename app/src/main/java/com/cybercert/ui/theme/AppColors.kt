package com.cybercert.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Single source of truth for all theme-aware colors in CyberCert. */
@Immutable
data class AppColors(val isDark: Boolean) {
    // Backgrounds
    val bg           = if (isDark) Color(0xFF0A0A0A) else Color(0xFFF0F2F5)
    val card         = if (isDark) Color(0xFF141414) else Color(0xFFFFFFFF)
    val cardBorder   = if (isDark) Color.Transparent else Color(0xFFE0E0E0)
    val dialogBg     = if (isDark) Color(0xFF141414) else Color(0xFFFFFFFF)
    val dialogBg2    = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
    val navBg        = if (isDark) Color(0xFF0D0D0D) else Color(0xFFFFFFFF)

    // Text
    val primaryText  = if (isDark) Color.White          else Color(0xFF0A0A0A)
    val secondaryText= if (isDark) Color(0xFFAAAAAA)    else Color(0xFF555555)

    // Accent — slightly darker in light mode for AA contrast on white
    val accent       = if (isDark) Color(0xFF00D4FF) else Color(0xFF0099BB)
    val accentOnFill = if (isDark) Color.Black       else Color.White   // text on filled accent bg
    val orange       = if (isDark) Color(0xFFFF6B35) else Color(0xFFE55A00)

    // Structural
    val divider      = if (isDark) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
    val inputBorder  = if (isDark) Color(0xFF333333) else Color(0xFFCCCCCC)
    val progressTrack= if (isDark) Color(0xFF2A2A2A) else Color(0xFFE8E8E8)

    // Input text (for OutlinedTextField)
    val inputText    = if (isDark) Color.White else Color(0xFF0A0A0A)

    // Filter chips
    val chipSelBg    = accent
    val chipSelText  = accentOnFill
    val chipUnselBg  = if (isDark) Color.Transparent else Color(0xFFFFFFFF)
    val chipUnselText= if (isDark) Color(0xFFCCCCCC) else Color(0xFF555555)
    val chipBorder   = if (isDark) Color(0xFF444444) else Color(0xFFCCCCCC)

    // Status badge
    val badgeNotStartedText   = if (isDark) Color(0xFFAAAAAA) else Color(0xFF777777)
    val badgeNotStartedBorder = if (isDark) Color(0xFF555555) else Color(0xFFBBBBBB)
    val badgeInProgressText   = accent
    val badgeInProgressBorder = accent
    val badgeCompletedText    = if (isDark) Color(0xFF00C853) else Color(0xFF00A040)
    val badgeCompletedBorder  = if (isDark) Color(0xFF00C853) else Color(0xFF00A040)

    // Career path circles
    val circleCompleted  = if (isDark) Color(0xFF00C853) else Color(0xFF00A040)
    val circleInProgress = accent
    val circleDefault    = if (isDark) Color(0xFF444444) else Color(0xFFCCCCCC)
    val circleDefaultText= if (isDark) Color(0xFF666666) else Color(0xFF888888)

    // Misc
    val deleteText   = if (isDark) Color(0xFF888888) else Color(0xFF888888)
    val subtleCard   = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF7F7F7)
}
