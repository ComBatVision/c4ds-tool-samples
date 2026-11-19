package vision.combat.c4.ds.sample.gallery.hostservices.ui

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import earth.worldwind.geom.Location
import kotlinx.coroutines.launch
import org.kodein.di.compose.rememberInstance
import vision.combat.c4.ds.sample.gallery.R
import vision.combat.c4.ds.sdk.tool.ToolContext
import vision.combat.c4.ds.sdk.ui.component.IconWithText
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavTopAppBar
import vision.combat.c4.ds.sdk.ui.component.button.Button
import vision.combat.c4.ds.sdk.ui.notification.Notifier
import vision.combat.c4.ds.sdk.ui.notification.post
import vision.combat.c4.ds.sdk.ui.platform.InAppNotificationManager
import vision.combat.c4.ds.sdk.ui.platform.LocalShareManager
import vision.combat.c4.unit.CoordinateSystemFormat
import java.io.File

@Composable
internal fun HostServicesWindow(toolContext: ToolContext) {
    WindowScaffold(
        topAppBar = { BackNavTopAppBar(title = stringResource(R.string.host_services_tool_name)) },
        content = { HostServicesContent(toolContext) },
    )
}

@Composable
private fun ColumnScope.HostServicesContent(toolContext: ToolContext) {
    Text(
        text = stringResource(R.string.host_services_desc),
        style = MaterialTheme.typography.body2,
        modifier = Modifier.padding(bottom = 16.dp),
    )

    ShareSection(toolContext)
    Divider(modifier = Modifier.padding(vertical = 16.dp))
    ClipboardSection()
    Divider(modifier = Modifier.padding(vertical = 16.dp))
    NotificationSection()
}

@Composable
private fun ColumnScope.ShareSection(toolContext: ToolContext) {
    val shareManager = LocalShareManager.current
    val scope = rememberCoroutineScope()
    val sampleLink = stringResource(R.string.host_services_sample_link)
    val sampleFileContent = stringResource(R.string.host_services_sample_file_content)

    Text(
        text = stringResource(R.string.host_services_share_title),
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Button(
        label = stringResource(R.string.host_services_share_link),
        onClick = { shareManager.shareLink(sampleLink) },
    )
    Button(
        label = stringResource(R.string.host_services_share_coordinates),
        onClick = {
            shareManager.shareCoordinates(
                Location.fromDegrees(SAMPLE_LATITUDE, SAMPLE_LONGITUDE),
                CoordinateSystemFormat.WGS84_DD,
            )
        },
    )
    Button(
        label = stringResource(R.string.host_services_share_file),
        onClick = {
            // "export/" is the sub-directory of the host's cache dir that its own FileProvider
            // <cache-path> declares (see c4ds-app/src/main/res/xml/file_paths.xml). ShareManager
            // resolves shareFile's argument through that FileProvider; a file written directly
            // to the cache dir *root* isn't covered by any declared path, so getUriForFile fails
            // and ShareManager falls back to a raw file:// Uri, which the OS then blocks with a
            // FileUriExposedException. Writing under "export/" keeps this demo on the supported,
            // crash-free path.
            val exportDir = File(toolContext.cacheDir, "export")
            exportDir.mkdirs()
            val file = File(exportDir, SAMPLE_FILE_NAME)
            file.writeText(sampleFileContent)
            shareManager.shareFile(file, "text/plain")
        },
    )
    Button(
        label = stringResource(R.string.host_services_share_image),
        onClick = {
            scope.launch {
                val bitmap = Bitmap.createBitmap(SAMPLE_BITMAP_SIZE, SAMPLE_BITMAP_SIZE, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.RED)
                shareManager.shareImage(bitmap, "gallery_host_services_")
            }
        },
    )
}

@Composable
private fun ColumnScope.ClipboardSection() {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val sampleText = stringResource(R.string.host_services_sample_text)
    val copiedLabel = stringResource(R.string.host_services_clipboard_copied)
    var copied by remember { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.host_services_clipboard_title),
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Button(
        label = stringResource(R.string.host_services_copy_text),
        onClick = {
            scope.launch {
                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(CLIP_LABEL, sampleText)))
                copied = true
            }
        },
    )
    if (copied) {
        Text(
            text = copiedLabel,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ColumnScope.NotificationSection() {
    val notifier by rememberInstance<Notifier>()
    val postedLabel = stringResource(R.string.host_services_notification_posted)
    var posted by remember { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.host_services_notification_title),
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Button(
        label = stringResource(R.string.host_services_post_notification),
        onClick = {
            val inAppModel = InAppNotificationManager.InAppNotificationModel(
                data = SampleNotificationData,
                // Resources are read INSIDE content, not hoisted into this window's
                // composition. The host composes this lambda itself, next to the map — but the
                // notifier above came from this tool's DI graph (AbstractTool binds a
                // tool-scoped one), so the host wraps the content in this tool's environment:
                // LocalContext is the tool's context, and R.drawable/R.string resolve against
                // this plugin's own resource table. Passing the same ids to the host's context
                // would be a wrong resource at best and a Resources.NotFoundException at worst.
                // Reading them here also keeps the notification config-reactive: it re-resolves
                // when the host's app language or night mode changes while it is on screen.
                content = {
                    IconWithText(
                        painter = painterResource(R.drawable.ic_notification),
                        text = stringResource(R.string.host_services_notification_content),
                        contentDescription = stringResource(R.string.host_services_notification_icon_desc),
                    )
                },
            )
            // A transient notification only: no system notification and no watch, so the host shows it over the map
            // while the app is in the foreground. Posting again under the same id updates it.
            notifier.post(SAMPLE_NOTIFICATION_ID) { inApp { transient(inAppModel) } }
            posted = true
        },
    )
    if (posted) {
        Text(
            text = postedLabel,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/**
 * Minimal [InAppNotificationManager.InAppNotificationData] for this sample's transient notification: no
 * text-to-speech announcement and no id, since the host finds the notification by the key it is posted under.
 */
private object SampleNotificationData : InAppNotificationManager.InAppNotificationData {
    override val textToSpeech: String? = null
}

private const val SAMPLE_NOTIFICATION_ID = 1
private const val SAMPLE_FILE_NAME = "host_services_sample.txt"
private const val SAMPLE_BITMAP_SIZE = 64
private const val SAMPLE_LATITUDE = 50.4501
private const val SAMPLE_LONGITUDE = 30.5234
private const val CLIP_LABEL = "host_services_sample_text"
