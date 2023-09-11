package it.unibs.mp.horace.backend.firebase

import it.unibs.mp.horace.backend.firebase.models.Notification
import java.time.LocalDate

class NotificationDay(
    val date: LocalDate, val notifications: List<Notification>
) {
    companion object {
        fun fromNotifications(notifications: List<Notification>): List<NotificationDay> {
            // Sort entries by least recent startTime, group by date, sort by date
            // and finally map to JournalDay
            return notifications.sortedBy { it.timeSent.toLocalTime() }
                .groupBy { it.timeSent.toLocalDate() }.toSortedMap(reverseOrder())
                .map { NotificationDay(it.key, it.value) }
        }
    }
}
