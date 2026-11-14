package vision.combat.c4.ds.sample.gallery.service

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.tool.ToolNotificationManager
import vision.combat.c4.ds.sdk.ui.notification.Notifier
import vision.combat.c4.ds.sdk.ui.platform.IntentProvider

/**
 * Minimal [ToolNotificationManager] that mirrors the service's unread count onto the tool-list badge.
 *
 * [counter] is the public API consumed by the SDK to update the badge numeral displayed next to this
 * tool in the tool list. It is derived from [BadgeCounterService.unreadCount] — the same pattern the
 * host uses, where a notification manager maps a session source of truth (e.g. an interactor's
 * `totalUnreadCount`) into `counter`. The SDK renders the badge only while the count is greater than
 * 0 and the tool is inactive, so "Mark all read" (which zeroes the count) makes the badge disappear.
 *
 * [service] is resolved from the service's own DI graph, into which AbstractToolService binds itself
 * via `bindErasedInstance`. [notifier] comes from the same graph, like [intentProvider].
 */
internal class ServiceNotificationManager(
    context: ToolContext,
    intentProvider: IntentProvider,
    notifier: Notifier,
    service: BadgeCounterService,
) : ToolNotificationManager(context, intentProvider, notifier) {

    override val counter: StateFlow<UInt> = service.unreadCount
        .map { count -> count.toUInt() }
        .stateIn(coroutineScope, SharingStarted.Eagerly, 0u)

    override fun onDestroy() = Unit
}
