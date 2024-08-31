package com.example.jixiv.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

//头像框
@Composable
fun AvatarWithFrame(url: String, size: Dp) {
    Card(
        shape = CircleShape,
        modifier = Modifier
            .size(size)
            .clickable {
            }
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(size)
        )
    }
}