package com.example.zenglow


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.zenglow.data.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialog(
    state: GroupState,
    onEvent: (GroupEvent) -> Unit,
    modifier: Modifier = Modifier,
    group: Group
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(GroupEvent.HideRenameDialog)
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(GroupEvent.RenameGroup(group))
                }) {
                    Text(text="Save group")
                }
            }
        },
        title = { Text(text= "Rename Group")},
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = state.name,
                    onValueChange = {
                        onEvent(GroupEvent.SetName(it))
                    },
                    placeholder = {
                        Text(text = "Group name")
                    }
                )
            }
        }
    )
}