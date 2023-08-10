package de.kassel.cc22023.roadtrip.ui.packing.item_list_view

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ItemCardButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}