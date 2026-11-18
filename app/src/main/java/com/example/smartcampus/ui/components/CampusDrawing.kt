package com.example.smartcampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

/** Isometric architectural illustration in the campus header. */
@Composable fun CampusDrawing(modifier: Modifier) {
    Canvas(modifier) {
        val scale = minOf(size.width / 325f, size.height / 155f)
        withTransform({ translate(size.width - 325f * scale, (size.height - 155f * scale) / 2); scale(scale, scale, Offset.Zero) }) {
            fun point(x: Float, y: Float, z: Float = 0f) = Offset(152 + x - y, 46 + (x + y) * .39f - z)
            fun line(a: Offset, b: Offset, color: Color, width: Float = 1f) = drawLine(color, a, b, width)
            fun face(a: Offset, b: Offset, c: Offset, d: Offset, color: Color) {
                val corners = listOf(a, b, c, d)
                val path = Path().apply { moveTo(corners[0].x, corners[0].y); corners.drop(1).forEach { lineTo(it.x,it.y) }; close() }
                drawPath(path,color); drawPath(path,Color(0xFF90B9EF),style = Stroke(1.1f))
            }
            for (i in -4..8) {
                val k = i * 16f
                line(point(k,-45f),point(k,124f),Color(0x444E93D5))
                line(point(-65f,k),point(147f,k),Color(0x444E93D5))
            }
            fun building(x: Float,y: Float,w: Float,d: Float,h: Float) {
                face(point(x,y+d),point(x+w,y+d),point(x+w,y+d,h),point(x,y+d,h),color = Color(0xFF315A95))
                face(point(x+w,y),point(x+w,y+d),point(x+w,y+d,h),point(x+w,y,h),color = Color(0xFF193F76))
                face(point(x,y,h),point(x+w,y,h),point(x+w,y+d,h),point(x,y+d,h),color = Color(0xFF5B88C0))
                for (i in 5 until w.toInt()-2 step 8) line(point(x+i,y+d,6f),point(x+i,y+d,h-5),Color(0xFF99D5F2),2f)
            }
            building(-45f,0f,65f,26f,28f)
            building(42f,-10f,58f,36f,36f)
            building(0f,62f,86f,27f,23f)
            val pin = point(65f,4f,39f)
            line(pin, pin - Offset(0f,16f),Color(0xFF72E7DF),2f)
            drawCircle(Color(0xFF72E7DF),5f,pin-Offset(0f,17f))
        }
    }
}

