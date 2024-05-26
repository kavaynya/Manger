package com.san.kir.statistic.ui.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.san.kir.core.compose.BigSpacer
import com.san.kir.core.compose.Dimensions
import com.san.kir.core.compose.NavigationButton
import com.san.kir.core.compose.ScreenContent
import com.san.kir.core.compose.animation.TopAnimatedVisibility
import com.san.kir.core.compose.horizontalInsetsPadding
import com.san.kir.core.compose.topBar
import com.san.kir.core.utils.bytesToMb
import com.san.kir.core.utils.flow.collectAsStateWithLifecycle
import com.san.kir.core.utils.format
import com.san.kir.core.utils.formatTime
import com.san.kir.core.utils.viewModel.stateHolder
import com.san.kir.data.models.main.Statistic
import com.san.kir.statistic.R
import com.san.kir.statistic.utils.buildPreparedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticScreen(navigateUp: () -> Unit, itemId: Long = -1L, mangaId: Long = -1L) {
    val holder: StatisticStateHolder = stateHolder { StatisticViewModel(itemId, mangaId) }
    val state by holder.state.collectAsStateWithLifecycle()

    ScreenContent(
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        additionalPadding = Dimensions.default,
        topBar = topBar(
            navigationButton = NavigationButton.Back(navigateUp),
            title = state.mangaName,
        ),
    ) {
        Column(modifier = Modifier.horizontalInsetsPadding()) {

            TopAnimatedVisibility(
                state.item.lastTime > 0
                        || state.item.lastPages > 0
                        || state.item.lastChapters > 0
            ) {
                Column {
                    Label(R.string.last_session)

                    MainText(
                        timePagesData(
                            time = state.item.lastTime,
                            pages = state.item.lastPages,
                            chapters = state.item.lastChapters
                        ),
                        modifier = Modifier.padding(top = Dimensions.smallest),
                    )

                    TopAnimatedVisibility(state.item.lastPages > 0 && state.item.lastTime > 0) {
                        MainText(
                            speedReading(
                                lastPages = state.item.lastPages,
                                lastTime = state.item.lastTime
                            ),
                            modifier = Modifier.padding(top = Dimensions.smallest),
                        )
                    }

                    BigSpacer()
                }
            }

            Label(R.string.all)

            MainText(
                timePagesData(
                    time = state.item.allTime,
                    pages = state.item.allPages,
                    chapters = state.item.allChapters
                ),
                modifier = Modifier.padding(top = Dimensions.smallest),
            )

            TopAnimatedVisibility(state.item.allPages > 0 && state.item.allTime > 0) {
                MainText(
                    averageSpeedReading(state.item.allPages, state.item.allTime),
                    modifier = Modifier.padding(top = Dimensions.smallest),
                )
            }

            TopAnimatedVisibility(state.item.maxSpeed > 0) {
                MainText(
                    maxSpeedReading(state.item.maxSpeed),
                    modifier = Modifier.padding(top = Dimensions.smallest),
                )
            }

            TopAnimatedVisibility(state.item.downloadSize > 0) {
                MainText(
                    downloadData(state.item.downloadSize, state.item.downloadTime),
                    modifier = Modifier.padding(top = Dimensions.smallest),
                )
            }

            TopAnimatedVisibility(state.item.openedTimes > 0) {
                MainText(
                    readTimes(state.item.openedTimes),
                    modifier = Modifier.padding(top = Dimensions.smallest),
                )
            }

            TopAnimatedVisibility(state.item.allTime > 0 && state.item.openedTimes > 0) {
                MainText(
                    averageSession(state.item),
                    modifier = Modifier.padding(top = Dimensions.smallest),
                )
            }
        }
    }
}


@Composable
private fun Label(idRes: Int) {
    Text(
        text = stringResource(idRes),
        modifier = Modifier.padding(start = Dimensions.default),
        fontSize = 15.sp,
        fontStyle = FontStyle.Italic
    )
}

@Composable
private fun MainText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = modifier.padding(top = Dimensions.quarter),
        color = color,
        fontSize = 19.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun timePagesData(time: Long, pages: Int, chapters: Int): AnnotatedString {

    if (time == 0L) return AnnotatedString(stringResource(R.string.no_time))

    val timeString = time.formatTime()
    if (pages == 0) {
        return buildPreparedString(stringResource(R.string.time_nothing, timeString), timeString)
    }

    val pluralPages = pluralStringResource(R.plurals.pages, pages)
    if (chapters == 0) {
        return buildPreparedString(
            stringResource(R.string.time_with_pages, timeString, pages, pluralPages),
            timeString, "$pages $pluralPages"
        )
    }

    val pluralChapters = pluralStringResource(R.plurals.chapters, chapters)
    return buildPreparedString(
        stringResource(
            R.string.time_with_pages_chapters,
            timeString, pages, pluralPages, chapters, pluralChapters
        ),
        timeString, "$pages $pluralPages", "$chapters $pluralChapters"
    )
}

@Composable
private fun speedReading(lastPages: Int, lastTime: Long): AnnotatedString {
    val speed = (lastPages / (lastTime.toFloat() / 60)).toInt()
    if (speed == 0) {
        return buildPreparedString(
            stringResource(R.string.speed_nothing), stringResource(R.string.nothing),
        )
    }

    val pluralPages = pluralStringResource(R.plurals.pages, speed)
    return buildPreparedString(
        stringResource(R.string.speed_reading, speed, pluralPages),
        "$speed $pluralPages"
    )
}

@Composable
private fun averageSpeedReading(allPages: Int, allTime: Long): AnnotatedString {
    val speed = (allPages / (allTime.toFloat() / 60)).toInt()
    if (speed == 0) {
        return buildPreparedString(
            stringResource(R.string.average_speed_nothing), stringResource(R.string.nothing),
        )
    }

    val pluralPages = pluralStringResource(R.plurals.pages, speed)
    return buildPreparedString(
        stringResource(R.string.average_speed_reading, speed, pluralPages),
        "$speed $pluralPages"
    )
}

@Composable
private fun maxSpeedReading(maxSpeed: Int): AnnotatedString {
    val pluralPages = pluralStringResource(R.plurals.pages, maxSpeed)
    return buildPreparedString(
        stringResource(R.string.max_speed, maxSpeed, pluralPages),
        "$maxSpeed $pluralPages"
    )
}

@Composable
private fun downloadData(downloadSize: Long, downloadTime: Long): AnnotatedString {
    val size = bytesToMb(downloadSize).format()
    val timeString = (downloadTime / 1000).formatTime()
    return buildPreparedString(
        stringResource(R.string.download_data, size, timeString), size, timeString
    )
}

@Composable
private fun readTimes(openedTimes: Int): AnnotatedString {
    val times = pluralStringResource(R.plurals.times, openedTimes)
    return buildPreparedString(
        stringResource(R.string.opening_times, openedTimes, times), "$openedTimes $times"
    )
}

@Composable
private fun averageSession(item: Statistic): AnnotatedString {
    if (item.openedTimes == 0) {
        return AnnotatedString(
            stringResource(
                R.string.session_time_pages,
                0L.formatTime(), 0, pluralStringResource(R.plurals.pages, item.allPages)
            )
        )
    }

    val time = (item.allTime / item.openedTimes).formatTime()
    val avgPages = item.allPages / item.openedTimes
    val pluralPages = pluralStringResource(R.plurals.pages, avgPages)
    return buildPreparedString(
        stringResource(R.string.session_time_pages, time, avgPages, pluralPages),
        time, "$avgPages $pluralPages"
    )
}
