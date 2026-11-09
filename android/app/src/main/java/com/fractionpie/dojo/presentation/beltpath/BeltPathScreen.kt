package com.fractionpie.dojo.presentation.beltpath

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fractionpie.dojo.R
import com.fractionpie.dojo.core.di.AppViewModelFactory
import com.fractionpie.dojo.core.theme.Accent
import com.fractionpie.dojo.core.theme.Background
import com.fractionpie.dojo.core.theme.BorderSand
import com.fractionpie.dojo.core.theme.TextMuted
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.theme.TextSecondary
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.ScreenHeader
import com.fractionpie.dojo.core.ui.SecondaryButton

@Composable
fun BeltPathScreen(
    onBelt: (Int) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val beltPathViewModel: BeltPathViewModel = viewModel(factory = AppViewModelFactory())
    val state by beltPathViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        beltPathViewModel.refresh()
    }

    BeltPathContent(
        state = state,
        onBelt = onBelt,
        onMenu = onMenu,
        modifier = modifier,
    )
}

@Composable
private fun BeltPathContent(
    state: BeltPathUiState,
    onBelt: (Int) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xE6FFF6E9), Color(0xF7F3D9B8)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            ScreenHeader(
                title = stringResource(R.string.belt_path),
                backDescription = stringResource(R.string.cd_back),
                onBack = onMenu,
                trailing = {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp),
                    )
                },
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.rows, key = { row -> row.index }) { row ->
                    BeltRow(row = row, onBelt = onBelt)
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
private fun BeltRow(
    row: BeltRowUi,
    onBelt: (Int) -> Unit,
) {
    val lockedLabel = stringResource(R.string.state_locked)
    val beltColor = Color(row.color)
    val rowModifier = if (row.unlocked) {
        Modifier.clickable { onBelt(row.index) }
    } else {
        Modifier.semantics { stateDescription = lockedLabel }
    }

    ParchmentCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .alpha(if (row.unlocked) 1f else 0.55f)
            .then(rowModifier),
        borderColor = beltColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (row.unlocked) beltColor else BorderSand,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (row.unlocked) {
                    Text(
                        text = (row.index + 1).toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Background,
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_lock),
                        contentDescription = lockedLabel,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(row.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(row.skillRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            if (row.mastered) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(
                    text = stringResource(R.string.progress_value, row.clean, row.target),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted,
                )
            }
        }
    }
}
