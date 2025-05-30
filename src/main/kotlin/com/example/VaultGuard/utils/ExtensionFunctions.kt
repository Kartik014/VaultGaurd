package com.example.VaultGuard.utils

import com.example.VaultGuard.utils.enums.FrequencyCrons

object ExtensionFunctions {
    fun <T : Enum<T>> T.string(): String {
        return this.name.lowercase()
    }

    fun FrequencyCrons.toCron(): String {
        return when (this) {
            FrequencyCrons.HOURLY -> "0 0 * * * *"               // Every hour
            FrequencyCrons.DAILY -> "0 0 0 * * *"                // Every day at midnight
            FrequencyCrons.WEEKLY -> "0 0 0 * * MON"             // Every Monday at midnight
            FrequencyCrons.FORTNIGHT -> "0 0 0 1,15 * *"         // 1st and 15th of each month
            FrequencyCrons.MONTHLY -> "0 0 0 1 * *"              // 1st of every month
            FrequencyCrons.QUATERLY -> "0 0 0 1 1,4,7,10 *"      // Jan 1, Apr 1, Jul 1, Oct 1
            FrequencyCrons.HALF_YEARLY -> "0 0 0 1 1,7 *"        // Jan 1 and Jul 1
            FrequencyCrons.YEARLY -> "0 0 0 1 1 *"               // Jan 1 every year
        }
    }
}