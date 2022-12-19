package com.hashem.mousavi.puzzle15

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hashem.mousavi.puzzle15.ui.Puzzle15Board
import com.hashem.mousavi.puzzle15.ui.theme.Orange
import com.hashem.mousavi.puzzle15.ui.theme.Puzzle15Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: Puzzle15ViewModel by viewModels()
            Puzzle15Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Orange)
                ) {

                    Puzzle15Board(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        cellInfos = viewModel.boardData,
                        shakeState = viewModel.shakeFlow,
                        onCellClicked = { cellInfo ->
                            viewModel.onCellClicked(cellInfo)
                        }
                    )
                }
            }
        }
    }
}



