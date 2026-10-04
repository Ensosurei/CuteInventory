package com.example.cuteinventory.core.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

data class PatternDot(
    val id: Int,
    val center: Offset
)

@Composable
fun PatternLockView(
    key: Any = Unit,
    modifier: Modifier = Modifier,
    clearTrigger: Int = 0,
    nodeRadius: Float = 36f,
    touchRadius: Float = 80f,
    onPatternComplete: (String) -> Unit
) {
    val selectedNodes = remember { mutableStateListOf<Int>() }
    // Clear canvas lines when clearTrigger changes
    LaunchedEffect(clearTrigger) {
        selectedNodes.clear()
    }

    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }
    val nodes = remember { mutableMapOf<Int, Offset>() }

    val dotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val selectedColor = MaterialTheme.colorScheme.primary
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(32.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectedNodes.clear()
                        nodes.forEach { (id, center) ->
                            if (isPointInside(offset, center, touchRadius)) {
                                selectedNodes.add(id)
                                currentTouchPosition = offset
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        currentTouchPosition = change.position
                        nodes.forEach { (id, center) ->
                            if (isPointInside(change.position, center, touchRadius) && !selectedNodes.contains(id)) {
                                selectedNodes.add(id)
                            }
                        }
                    },
                    onDragEnd = {
                        if (selectedNodes.isNotEmpty()) {
                            onPatternComplete(selectedNodes.joinToString(","))
                        }
                        currentTouchPosition = null
                    },
                    onDragCancel = {
                        selectedNodes.clear()
                        currentTouchPosition = null
                    }
                )
            }
    ) {
        val sizeWidth = size.width
        val sizeHeight = size.height

        // Calculate 3x3 grid dots
        for (row in 0..2) {
            for (col in 0..2) {
                val id = row * 3 + col
                val x = (col + 0.5f) * (sizeWidth / 3)
                val y = (row + 0.5f) * (sizeHeight / 3)
                val center = Offset(x, y)
                nodes[id] = center

                val isSelected = selectedNodes.contains(id)
                drawCircle(
                    color = if (isSelected) selectedColor else dotColor,
                    radius = if (isSelected) nodeRadius * 1.3f else nodeRadius,
                    center = center
                )
            }
        }

        // Draw lines between connected nodes
        for (i in 0 until selectedNodes.size - 1) {
            val start = nodes[selectedNodes[i]]
            val end = nodes[selectedNodes[i + 1]]
            if (start != null && end != null) {
                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw active drag line to current touch location
        currentTouchPosition?.let { touchPos ->
            selectedNodes.lastOrNull()?.let { lastId ->
                nodes[lastId]?.let { start ->
                    drawLine(
                        color = lineColor,
                        start = start,
                        end = touchPos,
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun isPointInside(point: Offset, center: Offset, radius: Float): Boolean {
    val distance = sqrt((point.x - center.x).pow(2) + (point.y - center.y).pow(2))
    return distance <= radius
}