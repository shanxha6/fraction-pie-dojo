package com.fractionpie.dojo.presentation.scroll

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.PieCanvas
import com.fractionpie.dojo.core.ui.ScreenHeader
import com.fractionpie.dojo.core.ui.SecondaryButton
import com.fractionpie.dojo.domain.model.ScrollTerm

@Composable
fun ScrollScreen(
    onMenu: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollViewModel: ScrollViewModel = viewModel(factory = AppViewModelFactory())
    val state by scrollViewModel.state.collectAsStateWithLifecycle()

    ScrollContent(
        state = state,
        onMenu = onMenu,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun ScrollContent(
    state: ScrollUiState,
    onMenu: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33F3D9B8), Color.Transparent),
                        radius = 900f,
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ScreenHeader(
                    title = stringResource(R.string.scroll_title),
                    backDescription = stringResource(R.string.cd_back),
                    onBack = onBack,
                )
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .size(72.dp)
                        .graphicsLayer { alpha = 0.35f },
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.terms, key = { term -> term.termRes }) { term ->
                    TermCard(term = term)
                }
            }

            SecondaryButton(
                text = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun TermCard(term: ScrollTerm) {
    ParchmentCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = BorderSand,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(term.termRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(term.definitionRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            PieCanvas(
                slices = term.slices,
                filled = (0 until term.filled).toSet(),
                diameter = 52.dp,
                liftDp = 2.dp,
                highlight = Accent,
                description = stringResource(R.string.cd_pie, term.slices, term.filled),
            )
        }
    }
}
