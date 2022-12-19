package com.hashem.mousavi.puzzle15

import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashem.mousavi.puzzle15.ui.CellInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class Puzzle15ViewModel : ViewModel() {

    private val _boardData = mutableListOf<CellInfo>()
    val boardData: List<CellInfo> = _boardData

    private val _shakeFlow = MutableSharedFlow<CellInfo>()
    val shakeFlow = _shakeFlow.asSharedFlow()

    init {
        _boardData.addAll(createPuzzleData())

        viewModelScope.launch {
            var i = 100
            val emptyCell = boardData.find { it.number == 0 }!!
            while (i >= 0) {
                delay(10)
                val random = boardData.filter {
                    it != emptyCell && (it.actualColumn == emptyCell.actualColumn || it.actualRow == emptyCell.actualRow)
                }.random()
                onCellClicked(random)
                i--
            }
        }
    }

    private fun createPuzzleData(): List<CellInfo> {
        return mutableListOf<CellInfo>().apply {
            for (num in 1..15) {
                add(
                    CellInfo(
                        number = num,
                        row = (num - 1) / 4,
                        column = (num - 1) % 4,
                        size = 0 // must be set after measurement
                    )
                )
            }
            add(
                CellInfo(
                    number = 0,
                    row = 3,
                    column = 3,
                    size = 0 // must be set after measurement
                )
            )
        }
    }

    fun onCellClicked(clickedCell: CellInfo) {
        val size = clickedCell.size
        val emptyCell = _boardData.find { it.number == 0 } ?: return
        if (clickedCell == emptyCell) return

        if (clickedCell.actualRow == emptyCell.actualRow) {
            val cellsInRow =
                boardData.filter { it.actualRow == emptyCell.actualRow && it != emptyCell }

            val rightToLeft = clickedCell.actualColumn < emptyCell.actualColumn

            if (rightToLeft) {
                val cells = cellsInRow.filter {
                    it.actualColumn >= clickedCell.actualColumn &&
                            it.actualColumn < emptyCell.actualColumn
                }.sortedBy {
                    it.actualColumn
                }
                val xOffsetForEmptyCell = -cells.size * size
                cells.forEach {
                    it.offsetState += IntOffset(x = size, y = 0)
                }
                emptyCell.offsetState += IntOffset(x = xOffsetForEmptyCell, y = 0)
            } else {
                val cells = cellsInRow.filter {
                    it.actualColumn <= clickedCell.actualColumn &&
                            it.actualColumn >= emptyCell.actualColumn
                }.sortedBy {
                    it.actualColumn
                }
                val xOffsetForEmptyCell = cells.size * size
                cells.forEach {
                    it.offsetState += IntOffset(x = -size, y = 0)
                }
                emptyCell.offsetState += IntOffset(x = xOffsetForEmptyCell, y = 0)
            }

        } else if (clickedCell.actualColumn == emptyCell.actualColumn) {
            val cellsInColumn =
                boardData.filter { it.actualColumn == emptyCell.actualColumn && it != emptyCell }

            val topToBottom = clickedCell.actualRow < emptyCell.actualRow

            if (topToBottom) {
                val cells = cellsInColumn.filter {
                    it.actualRow >= clickedCell.actualRow &&
                            it.actualRow < emptyCell.actualRow
                }.sortedBy {
                    it.actualRow
                }
                val yOffsetForEmptyCell = -cells.size * size
                cells.forEach {
                    it.offsetState += IntOffset(x = 0, y = size)
                }
                emptyCell.offsetState += IntOffset(x = 0, y = yOffsetForEmptyCell)
            } else {
                val cells = cellsInColumn.filter {
                    it.actualRow <= clickedCell.actualRow &&
                            it.actualRow > emptyCell.actualRow
                }.sortedBy {
                    it.actualRow
                }
                val yOffsetForEmptyCell = cells.size * size
                cells.forEach {
                    it.offsetState += IntOffset(x = 0, y = -size)
                }
                emptyCell.offsetState += IntOffset(x = 0, y = yOffsetForEmptyCell)
            }

        } else {
            viewModelScope.launch {
                _shakeFlow.emit(clickedCell)
            }

        }

    }
}