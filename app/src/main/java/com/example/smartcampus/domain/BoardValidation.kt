package com.example.smartcampus.domain

import java.time.LocalDate
import java.time.LocalTime

private val datePattern = Regex("""^\d{4}-\d{2}-\d{2}$""")
private val timePattern = Regex("""^\d{2}:\d{2}$""")

fun isValidDate(value: String): Boolean =
    datePattern.matches(value) && runCatching { LocalDate.parse(value) }.isSuccess

fun isValidTime(value: String): Boolean =
    timePattern.matches(value) && runCatching { LocalTime.parse(value) }.isSuccess

fun isNoticeValid(title: String, description: String, date: String): Boolean =
    title.isNotBlank() && description.isNotBlank() && isValidDate(date)

fun isEventValid(
    title: String,
    description: String,
    place: String,
    date: String,
    time: String
): Boolean =
    title.isNotBlank() &&
        description.isNotBlank() &&
        place.isNotBlank() &&
        isValidDate(date) &&
        isValidTime(time)
