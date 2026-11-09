package com.fractionpie.dojo.presentation.options

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import com.fractionpie.dojo.core.theme.Primary
import com.fractionpie.dojo.core.theme.SurfaceParchment
import com.fractionpie.dojo.core.theme.TextPrimary
import com.fractionpie.dojo.core.ui.ParchmentCard
import com.fractionpie.dojo.core.ui.ScreenHeader
import com.fractionpie.dojo.core.ui.SecondaryButton
import com.fractionpie.dojo.presentation.dialog.ResetDialog

@Composable
fun OptionsScreen(
    onMenu: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val optionsViewModel: OptionsViewModel = viewModel(factory = AppViewModelFactory())
    val state by optionsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.resetDone) {
        if (state.resetDone) onMenu()
    }

    OptionsContent(
        state = state,
        onSound = optionsViewModel::setSound,
        onHaptics = optionsViewModel::setHaptics,
        onLargeNumerals = optionsViewModel::setLargeNumerals,
        onRequestReset = optionsViewModel::requestReset,
        onMenu = onMenu,
        onBack = onBack,
        modifier = modifier,
    )

    if (state.resetRequested) {
        ResetDialog(
            onConfirm = optionsViewModel::confirmReset,
            onDismiss = optionsViewModel::dismissReset,
        )
    }
}

@Composable
private fun OptionsContent(
    state: OptionsUiState,
    onSound: (Boolean) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onLargeNumerals: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
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
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(SurfaceParchment, Color(0x00F3D9B8)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            ScreenHeader(
                title = stringResource(R.string.options_title),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SwitchRow(
                    label = stringResource(R.string.opt_sound),
                    checked = state.soundOn,
                    onChange = onSound,
                )
                SwitchRow(
                    label = stringResource(R.string.opt_haptics),
                    checked = state.hapticsOn,
                    onChange = onHaptics,
                )
                SwitchRow(
                    label = stringResource(R.string.opt_large),
                    checked = state.largeNumerals,
                    onChange = onLargeNumerals,
                )

                ParchmentCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Primary.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        OutlinedButton(
                            onClick = onRequestReset,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.reset_progress),
                                style = MaterialTheme.typography.labelLarge,
                                color = Primary,
                            )
                        }
                    }
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
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val onLabel = stringResource(R.string.state_on)
    val offLabel = stringResource(R.string.state_off)

    ParchmentCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics { stateDescription = if (checked) onLabel else offLabel },
        borderColor = BorderSand,
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.height(1.dp))
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Accent,
                    checkedThumbColor = Background,
                    uncheckedTrackColor = BorderSand,
                    uncheckedThumbColor = Background,
                ),
            )
        }
    }
}
