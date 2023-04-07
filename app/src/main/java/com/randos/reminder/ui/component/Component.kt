package com.randos.reminder.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.randos.reminder.ui.theme.*

@Composable
fun TransparentBackgroundTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderId: Int,
    isSingleLine: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 0.dp),
        placeholder = { Text(text = stringResource(id = placeHolderId)) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = transparent,
            unfocusedIndicatorColor = transparent,
            focusedIndicatorColor = transparent
        ),
        singleLine = isSingleLine
    )
}

@Composable
fun ReminderDefaultText(
    modifier: Modifier = Modifier,
    textResourceId: Int,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Text(
        text = stringResource(id = textResourceId),
        modifier = modifier
            .clickable(enabled = enabled) { onClick() },
        style = Typography.body1,
        color = if (enabled) fontColorBlack else fontColorGrey
    )

}

@Composable
fun ReminderDefaultDropdown(
    modifier: Modifier = Modifier,
    value: String,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(text = value, modifier = Modifier.clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.clickable { expanded = true }) {
            content()
        }
    }
}
