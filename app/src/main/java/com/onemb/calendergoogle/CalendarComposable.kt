package com.onemb.calendergoogle
// File: CalendarComposable.kt

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(onDateSelected: (LocalDate) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    fun updateSelectedDate(newDate: LocalDate) {
        selectedDate = newDate
        onDateSelected(newDate)
    }

    fun getFormattedMonthAndYear(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
            title = {
                Text(
                    text = getFormattedMonthAndYear(selectedDate),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { updateSelectedDate(selectedDate.minusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month"
                    )
                }
            },
                actions = {
                IconButton(onClick = { updateSelectedDate(selectedDate.plusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Localized description"
                    )
                }
            },
                colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            scrollBehavior = scrollBehavior
        )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Calendar Grid
            val daysInMonth = YearMonth.of(selectedDate.year, selectedDate.month).lengthOfMonth()
            val firstDayOfMonth = selectedDate.withDayOfMonth(1).dayOfWeek.value
            val offset = (firstDayOfMonth - 1 + 7) % 7

            LazyColumn {
                item {
                    // Weekdays Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        for (weekday in 1..7) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = DateFormatSymbols().shortWeekdays[(weekday % 7) + 1],
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                items((1..daysInMonth + offset).chunked(7)) { week ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        for (dayIndex in week.indices) {
                            val day = week[dayIndex]
                            val isCurrentMonth = day > offset
                            val dayToShow = if (isCurrentMonth) day - offset else null
                            val isSelected = isCurrentMonth && selectedDate.dayOfMonth == dayToShow

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RectangleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else if (isCurrentMonth) Color.White
                                        else Color.White
                                    )
                                    .clickable {
                                        dayToShow?.let {
                                            // Handle day click
                                            updateSelectedDate(selectedDate.withDayOfMonth(it))
                                        }
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                dayToShow?.let {
                                    Text(
                                        text = it.toString(),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Add empty boxes to the end of the last row to align with weekdays
                        if (week.size < 7) {
                            for (i in 1..(7 - week.size)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RectangleShape)
                                        .background(Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppContent() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalendarView(onDateSelected = { newDate ->
            selectedDate = newDate
        })

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selected Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))}")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AppContentPreview() {
    AppContent()
}
