package com.example.zenglow.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.zenglow.data.entities.Group
import com.example.zenglow.events.GroupEvent
/*
 FILE: DeleteGroupDialog.kt
 AUTHOR: Daniel Blaško <xblask05>
 DESCRIPTION: Dialog for deleting a group
 */
@Composable
fun DeleteGroupDialog(
    onEvent: (GroupEvent) -> Unit,
    modifier: Modifier = Modifier,
    group: Group
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(GroupEvent.HideDeleteDialog)
        },
        confirmButton = {
            Button(onClick = {
                onEvent(GroupEvent.DeleteGroup(group))
            }) {
                Text(text="Yes")
            }
        },
        dismissButton = {
            Button(onClick = {
                onEvent(GroupEvent.HideDeleteDialog)
            }) {
                Text(text="No")
            }
        },
        title = { Text(text= "Do you want to delete this group?")},
    )
}