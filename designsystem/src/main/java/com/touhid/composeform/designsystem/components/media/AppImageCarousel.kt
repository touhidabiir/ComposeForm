package com.touhid.composeform.designsystem.components.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing

data class AppCarouselPage(
    val caption: String,
    val content: @Composable BoxScope.() -> Unit,
)

// A swipeable, captioned set of pages (e.g. outlet photos) with a "current/total" counter
// overlay - takes a content slot per page rather than an image loader, since callers may not
// all resolve images the same way (URL, local asset, placeholder).
@Composable
fun AppImageCarousel(
    pages: List<AppCarouselPage>,
    modifier: Modifier = Modifier,
    pageHeight: Dp = 200.dp,
) {
    if (pages.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { pages.size })
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight),
        ) { page ->
            val item = pages[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(AppSpacing.Small)),
            ) {
                item.content(this)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppSpacing.Small),
                    shape = RoundedCornerShape(percent = 50),
                    color = Color.Black.copy(alpha = 0.55f),
                ) {
                    AppText(
                        text = "${page + 1}/${pages.size}",
                        style = AppTextStyle.Label,
                        override = AppTextOverride(color = Color.White),
                        modifier = Modifier.padding(horizontal = AppSpacing.Small, vertical = AppSpacing.ExtraSmall),
                    )
                }
            }
        }
        AppText(
            text = pages.getOrNull(pagerState.currentPage)?.caption.orEmpty(),
            style = AppTextStyle.BodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.Small),
            textAlign = TextAlign.Center,
        )
    }
}
