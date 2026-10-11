package vision.combat.c4.ds.sample.gallery.uicatalog.ui.detail

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import vision.combat.c4.ds.sample.gallery.uicatalog.ui.list.UiCatalogShowcase
import vision.combat.c4.ds.sdk.ui.component.WindowContentDefaults.ContentPaddings
import vision.combat.c4.ds.sdk.ui.component.WindowScaffold
import vision.combat.c4.ds.sdk.ui.component.bar.BackNavigationButton
import vision.combat.c4.ds.sdk.ui.component.bar.TopAppBar

@Composable
internal fun UiCatalogDetailScreen(entry: UiCatalogShowcase) {
    WindowScaffold(
        contentPaddingValues = if (entry != UiCatalogShowcase.LISTS) ContentPaddings else PaddingValues(0.dp),
        scrollable = entry != UiCatalogShowcase.LISTS,
        topAppBar = {
            TopAppBar(
                title = stringResource(entry.nameResId),
                navigationIcon = { BackNavigationButton() },
            )
        },
        content = { DetailContent(entry) },
    )
}

@Composable
private fun ColumnScope.DetailContent(entry: UiCatalogShowcase) {
    if (entry != UiCatalogShowcase.LISTS) {
        Text(
            text = stringResource(entry.descResId),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Divider(modifier = Modifier.padding(bottom = 16.dp))
    }

    when (entry) {
        UiCatalogShowcase.INLINE_MESSAGE -> InlineMessageDemo()
        UiCatalogShowcase.HEADER_FIELD -> HeaderFieldDemo()
        UiCatalogShowcase.EXPANDABLE_FIELD -> ExpandableFieldDemo()
        UiCatalogShowcase.FORM_FIELD_BOX -> FormFieldBoxDemo()
        UiCatalogShowcase.NESTED_FORM -> NestedFormDemo()
        UiCatalogShowcase.HOSTILITY_SELECTOR -> HostilitySelectorDemo()
        UiCatalogShowcase.BUTTONS -> ButtonsDemo()
        UiCatalogShowcase.TOP_APP_BAR -> TopAppBarDemo()
        UiCatalogShowcase.INPUTS -> InputsDemo()
        UiCatalogShowcase.CHIPS_FIELD -> ChipsFieldDemo()
        UiCatalogShowcase.SELECTION -> SelectionDemo()
        UiCatalogShowcase.FEEDBACK -> FeedbackDemo()
        UiCatalogShowcase.LISTS -> ListsDetailScreen()
    }
}
