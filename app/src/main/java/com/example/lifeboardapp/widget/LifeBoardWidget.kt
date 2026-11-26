package com.example.lifeboardapp.widget

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.lifeboardapp.MainActivity

private val Background = Color(0xFF171923)
private val White = Color(0xFFF6F4FF)
private val Muted = Color(0xFFA5A5B8)
private val Purple = Color(0xFFB8A7FF)
private val Blue = Color(0xFF9FBAFF)

class LifeBoardWidget : GlanceAppWidget() {

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val data = try {
            LifeBoardWidgetDataReader.load(context)
        } catch (e: Exception) {
            Log.e("LifeBoardWidget", "Data loading failed", e)
            LifeBoardWidgetData()
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(Background))
                    .padding(16.dp)
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.Vertical.Top,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {

                // HEADER WITH CIRCULAR LOGO
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight()
                    ) {
                        Text(
                            text = "LifeBoard",
                            style = TextStyle(
                                color = ColorProvider(White),
                                fontSize = 25.sp
                            )
                        )

                        Text(
                            text = "YOUR LIFE AT A GLANCE",
                            style = TextStyle(
                                color = ColorProvider(Muted),
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = GlanceModifier
                            .size(42.dp)
                            .background(ColorProvider(Color(0xFF302A45)))
                            .cornerRadius(21.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            provider = ImageProvider(
                                com.example.lifeboardapp.R.drawable.screenshot_2026_11_26_at_08_45_50
                            ),
                            contentDescription = "LifeBoard logo",
                            modifier = GlanceModifier.fillMaxSize()
                        )
                    }
                }

                Spacer(GlanceModifier.height(14.dp))

                // 1. HABITS
                TextFeature(
                    title = "HABITS",
                    content = "${data.completedHabitsToday} / ${data.activeHabits} completed today",
                    accent = Purple
                )

                // 2. TASKS
                TextFeature(
                    title = "TASKS",
                    content = data.pendingTasks
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(", ") { it }
                        ?: "All caught up!",
                    accent = Blue
                )

                // 3. CHECKLISTS
                TextFeature(
                    title = "CHECKLISTS",
                    content = data.checklistSummary
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" • ")
                        ?: "No active checklists",
                    accent = Purple
                )

                // 4. WISHLIST
                TextFeature(
                    title = "WISHLIST",
                    content = data.wishlistSummary
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" • ")
                        ?: "No wishlist items",
                    accent = Color(0xFFD8A9FF)
                )

                // 5. BUCKET LIST
                TextFeature(
                    title = "BUCKET LIST",
                    content = data.bucketListSummary
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(" • ")
                        ?: "No goals added",
                    accent = Color(0xFF8ED8C3)
                )

                // 6. JOURNAL
                TextFeature(
                    title = "JOURNAL",
                    content = data.latestJournalTitle
                        .ifBlank { "No journal entry" },
                    accent = Color(0xFFFFC98B)
                )

                Spacer(GlanceModifier.height(8.dp))

                Text(
                    text = "MAKE TODAY COUNT  →",
                    style = TextStyle(
                        color = ColorProvider(Purple),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
@GlanceComposable
private fun TextFeature(
    title: String,
    content: String,
    accent: Color
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(
                color = ColorProvider(accent),
                fontSize = 14.sp
            )
        )

        Spacer(GlanceModifier.height(2.dp))

        Text(
            text = content.take(65),
            style = TextStyle(
                color = ColorProvider(White),
                fontSize = 15.sp
            )
        )

        Spacer(GlanceModifier.height(6.dp))
    }
}
