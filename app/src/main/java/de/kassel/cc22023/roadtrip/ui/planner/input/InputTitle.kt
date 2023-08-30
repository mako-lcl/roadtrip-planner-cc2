package de.kassel.cc22023.roadtrip.ui.planner.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.kassel.cc22023.roadtrip.R

@Composable
fun InputTitle() {
    val context = LocalContext.current

    val image = painterResource(id = R.drawable.title)
    Image(
        painter = image,
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
        modifier = Modifier.height(160.dp)
    )
/*    Surface(
        shape = RoundedCornerShape(10),
        color = MaterialTheme.colorScheme.secondary,
    ) {
        Text(
            context.getString(R.string.planner_top_title),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .padding(8.dp),
        )
    }*/
}