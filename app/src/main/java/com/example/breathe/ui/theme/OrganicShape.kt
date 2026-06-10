package com.shanacoder.breathly.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class OrganicBlobShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.45f, 0f)
            cubicTo(w * 0.9f, h * 0.05f, w, h * 0.25f, w, h * 0.5f)
            cubicTo(w * 0.95f, h * 0.8f, w * 0.8f, h, w * 0.55f, h)
            cubicTo(w * 0.15f, h * 0.95f, 0f, h * 0.8f, 0f, h * 0.5f)
            cubicTo(0f, h * 0.2f, w * 0.1f, 0f, w * 0.45f, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Each seed produces a structurally distinct card shape — not just slightly distorted
 * versions of the same template. Corner radii, edge curvatures, and overall silhouettes
 * are independently designed per seed.
 */
class OrganicCardShape(private val seed: Int = 0) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val w = size.width
        val h = size.height

        val path = when (seed % 4) {
            // ── SHAPE 0 ─────────────────────────────────────────────────────────────────
            // "Pebble" — top-left corner is very large/round, bottom-right is tight.
            // Top edge sags slightly inward. Bottom bulges outward left.
            0 -> Path().apply {
                val rTL = 80f    // big top-left corner
                val rTR = 40f    // tight top-right
                val rBR = 70f    // medium bottom-right
                val rBL = 50f    // normal bottom-left

                moveTo(rTL, 0f)
                // Top edge: flat in middle, dips slightly right of center
                cubicTo(w * 0.35f, -h * 0.02f, w * 0.65f, h * 0.05f, w - rTR, 0f)
                // Top-right corner (tight)
                cubicTo(w - rTR * 0.4f, 0f, w, rTR * 0.4f, w, rTR)
                // Right edge: leans slightly inward in upper half, bows out at bottom
                cubicTo(w - h * 0.04f, h * 0.35f, w + h * 0.02f, h * 0.6f, w, h - rBR)
                // Bottom-right corner
                cubicTo(w, h - rBR * 0.4f, w - rBR * 0.4f, h, w - rBR, h)
                // Bottom edge: bulges outward (below baseline) toward left
                cubicTo(w * 0.6f, h + h * 0.05f, w * 0.3f, h + h * 0.03f, rBL, h)
                // Bottom-left corner
                cubicTo(rBL * 0.4f, h, 0f, h - rBL * 0.4f, 0f, h - rBL)
                // Left edge: slight S-curve — pulls left in lower half, right in upper
                cubicTo(-w * 0.03f, h * 0.6f, w * 0.04f, h * 0.35f, 0f, rTL)
                // Top-left corner (big, very rounded)
                cubicTo(0f, rTL * 0.4f, rTL * 0.4f, 0f, rTL, 0f)
                close()
            }

            // ── SHAPE 1 ─────────────────────────────────────────────────────────────────
            // "Shield" — top edge arcs upward (convex), bottom-right corner is cut off
            // more sharply. Left side leans outward giving a left-tilted feel.
            1 -> Path().apply {
                val rTL = 45f
                val rTR = 90f    // very large top-right
                val rBR = 35f    // small bottom-right
                val rBL = 65f    // large bottom-left

                moveTo(rTL, 0f)
                // Top edge: arcs upward in the center (convex bump)
                cubicTo(w * 0.3f, -h * 0.06f, w * 0.7f, -h * 0.06f, w - rTR, 0f)
                // Top-right corner (very large)
                cubicTo(w - rTR * 0.3f, 0f, w, rTR * 0.3f, w, rTR)
                // Right edge: straight — barely curves
                cubicTo(w + w * 0.01f, h * 0.4f, w - w * 0.01f, h * 0.65f, w, h - rBR)
                // Bottom-right corner (tight / sharp-ish)
                cubicTo(w, h - rBR * 0.45f, w - rBR * 0.45f, h, w - rBR, h)
                // Bottom edge: dips slightly downward past midpoint
                cubicTo(w * 0.62f, h + h * 0.03f, w * 0.38f, h - h * 0.02f, rBL, h)
                // Bottom-left corner (large)
                cubicTo(rBL * 0.4f, h, 0f, h - rBL * 0.4f, 0f, h - rBL)
                // Left edge: bows outward (to the left) in lower half
                cubicTo(-w * 0.05f, h * 0.65f, -w * 0.02f, h * 0.3f, 0f, rTL)
                // Top-left corner
                cubicTo(0f, rTL * 0.45f, rTL * 0.45f, 0f, rTL, 0f)
                close()
            }

            // ── SHAPE 2 ─────────────────────────────────────────────────────────────────
            // "Wide Blob" — top corners are both small/tight, bottom is very wide and round.
            // The card appears to flare out as it goes down.
            2 -> Path().apply {
                val rTL = 35f   // small top-left
                val rTR = 35f   // small top-right
                val rBR = 85f   // very large bottom-right
                val rBL = 85f   // very large bottom-left

                moveTo(rTL, 0f)
                // Top edge: slightly dips in the middle (concave)
                cubicTo(w * 0.3f, h * 0.04f, w * 0.72f, h * 0.04f, w - rTR, 0f)
                // Top-right corner (tight)
                cubicTo(w - rTR * 0.4f, 0f, w, rTR * 0.4f, w, rTR)
                // Right edge: flares outward strongly in lower half
                cubicTo(w + w * 0.01f, h * 0.3f, w + w * 0.04f, h * 0.58f, w, h - rBR)
                // Bottom-right corner (very round, large)
                cubicTo(w, h - rBR * 0.4f, w - rBR * 0.4f, h, w - rBR, h)
                // Bottom edge: perfectly flat / very gentle outward bow
                cubicTo(w * 0.68f, h + h * 0.01f, w * 0.32f, h + h * 0.01f, rBL, h)
                // Bottom-left corner (very round, large)
                cubicTo(rBL * 0.4f, h, 0f, h - rBL * 0.4f, 0f, h - rBL)
                // Left edge: flares outward in lower half (mirror of right)
                cubicTo(-w * 0.04f, h * 0.58f, -w * 0.01f, h * 0.3f, 0f, rTL)
                // Top-left corner (tight)
                cubicTo(0f, rTL * 0.4f, rTL * 0.4f, 0f, rTL, 0f)
                close()
            }

            // ── SHAPE 3 ─────────────────────────────────────────────────────────────────
            // "Lopsided" — right side has a gentle inward dent in the middle,
            // top-right is very large, bottom-left is very large; creates an asymmetric
            // diagonal feel.
            else -> Path().apply {
                val rTL = 55f
                val rTR = 55f
                val rBR = 45f
                val rBL = 90f   // dominant large corner

                moveTo(rTL, 0f)
                // Top edge: rises slightly left of center, dips right of center
                cubicTo(w * 0.25f, -h * 0.04f, w * 0.60f, h * 0.06f, w - rTR, 0f)
                // Top-right corner
                cubicTo(w - rTR * 0.45f, 0f, w, rTR * 0.45f, w, rTR)
                // Right edge: dips inward (concave dent) around 45% down
                cubicTo(w - w * 0.07f, h * 0.3f, w - w * 0.07f, h * 0.62f, w, h - rBR)
                // Bottom-right corner (tight)
                cubicTo(w, h - rBR * 0.45f, w - rBR * 0.45f, h, w - rBR, h)
                // Bottom edge: dips down in right half, rises to meet large bottom-left corner
                cubicTo(w * 0.65f, h + h * 0.04f, w * 0.4f, h + h * 0.02f, rBL, h)
                // Bottom-left corner (very large, dominant)
                cubicTo(rBL * 0.35f, h, 0f, h - rBL * 0.35f, 0f, h - rBL)
                // Left edge: bows slightly outward in upper half
                cubicTo(-w * 0.02f, h * 0.55f, w * 0.03f, h * 0.28f, 0f, rTL)
                // Top-left corner
                cubicTo(0f, rTL * 0.45f, rTL * 0.45f, 0f, rTL, 0f)
                close()
            }
        }

        return Outline.Generic(path)
    }
}


