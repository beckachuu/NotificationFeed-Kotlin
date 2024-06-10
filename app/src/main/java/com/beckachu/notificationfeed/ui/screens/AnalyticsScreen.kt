package com.beckachu.notificationfeed.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.beckachu.notificationfeed.Const
import com.beckachu.notificationfeed.data.entities.NotificationEntity
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.util.Calendar

@Composable
fun AnalyticsScreen(
    notifications: List<NotificationEntity>
) {
    val totalNotifications = notifications.size
    val chartColor = Color(0xFF315DA8)

    val currentTime = System.currentTimeMillis()
    val recentNotifications = notifications.filter {
        val timeDifference = currentTime - it.postTime
        timeDifference <= 24 * 60 * 60 * 1000 // 24 hours in milliseconds
    }

    val notificationsByApp = recentNotifications.groupBy { it.appName ?: it.packageName }
    val notificationsByHour = (0..23).associateWith { hour ->
        recentNotifications.filter {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.postTime
            calendar.get(Calendar.HOUR_OF_DAY) == hour
        }
    }

    val appNames = notificationsByApp.keys.toList().sorted()
    val appBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> appNames[x.toInt() % appNames.size] }

    val hoursOfDay = notificationsByHour.keys.sorted().map { it.toString() }
    val hourBottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _ -> hoursOfDay[x.toInt() % hoursOfDay.size] }


    val refreshData = remember { mutableStateOf(false) }

    val appModelProducer = remember { ChartEntryModelProducer() }
    val hourModelProducer = remember { ChartEntryModelProducer() }

    val appModelData = remember { mutableStateListOf(listOf<FloatEntry>()) }
    val hourModelData = remember { mutableStateListOf(listOf<FloatEntry>()) }

    val appLineComponent = remember { arrayListOf<LineComponent>() }
    val hourLineSpec = remember { arrayListOf<LineChart.LineSpec>() }

    val appScrollState = rememberChartScrollState()
    val hourScrollState = rememberChartScrollState()

    LaunchedEffect(Unit) {
        if (!refreshData.value) {
            appModelData.clear()
            hourModelData.clear()
            appLineComponent.clear()
            hourLineSpec.clear()

            val appDataPoints =
                notificationsByApp.entries.mapIndexed { index, (app, notifications) ->
                    FloatEntry(index.toFloat(), notifications.size.toFloat())
                }
            val hourDataPoints =
                notificationsByHour.entries.mapIndexed { index, (hour, notifications) ->
                    FloatEntry(index.toFloat(), notifications.size.toFloat())
                }
            appLineComponent.add(
                LineComponent(
                    color = chartColor.toArgb(),
                    dynamicShader = DynamicShaders.fromBrush(
                        brush = Brush.verticalGradient(
                            listOf(
                                chartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                chartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )
                        )
                    ),
                    thicknessDp = 17F,

                    )
            )
            hourLineSpec.add(
                LineChart.LineSpec(
                    lineColor = chartColor.toArgb(),
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        brush = Brush.verticalGradient(
                            listOf(
                                chartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                chartColor.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )
                        )
                    )
                )
            )

            appModelData.add(appDataPoints)
            hourModelData.add(hourDataPoints)

            appModelProducer.setEntries(appModelData)
            hourModelProducer.setEntries(hourModelData)

            refreshData.value = true
        }
    }


    Column(modifier = Modifier.padding(Const.LEFT_PADDING)) {
//        Text(
//            text = "Total notifications: $totalNotifications",
//            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
//            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )

        Text(
            text = "Notifications by App:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Chart(
            chart = columnChart(
                columns = appLineComponent
            ),
            chartModelProducer = appModelProducer,
            chartScrollState = appScrollState,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(
                valueFormatter = appBottomAxisValueFormatter,
                itemPlacer = AxisItemPlacer.Horizontal.default(
                    shiftExtremeTicks = false
                ),
                guideline = null,
                tickLength = 0.dp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            text = "Notifications by Hour:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Chart(
            chart = lineChart(
                lines = hourLineSpec
            ),
            chartModelProducer = hourModelProducer,
            chartScrollState = hourScrollState,
            startAxis = rememberStartAxis(
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 5),
                guideline = null
            ),
            bottomAxis = rememberBottomAxis(
                valueFormatter = hourBottomAxisValueFormatter,
                itemPlacer = AxisItemPlacer.Horizontal.default(
                    shiftExtremeTicks = false
                ),
                guideline = null,
                tickLength = 0.dp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

