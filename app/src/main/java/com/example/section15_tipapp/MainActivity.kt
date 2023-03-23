package com.example.section15_tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.section15_tipapp.Widgets.RoundIconButton
import com.example.section15_tipapp.components.InputField
import com.example.section15_tipapp.ui.theme.Section15_TipAppTheme
import com.example.section15_tipapp.ui.theme.Shapes
import com.example.section15_tipapp.Widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Section15_TipAppTheme {
                val totalPerPerson = remember {
                    mutableStateOf(0.0)
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        CreateTotalPerPerson(totalPerPerson.value)
                        Spacer(modifier = Modifier.height(15.dp))
                        BillInputField { billAmount ->
                            totalPerPerson.value = billAmount
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateTotalPerPerson(totalPerPerson: Double = 0.0) {
    Card(shape = Shapes.medium, backgroundColor = Color(0xFFE3C7FD)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", fontWeight = FontWeight.Bold)
            Text(text = "$$total", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillInputField(
    //modifier: Modifier = Modifier,
    onValChange: (Double) -> Unit = {}
) {
    val splitAmount = remember {
        mutableStateOf(1)
    }
    val tipPercent = remember {
        mutableStateOf(0f)
    }
    val percent = (tipPercent.value * 100).toInt()
    val tip = remember {
        mutableStateOf(0)
    }
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        shape = Shapes.medium,
        backgroundColor = Color.White,
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(
                        ((totalBillState.value.trim()
                            .toDouble() + tip.value) / splitAmount.value)
                    )

                    keyboardController?.hide()
                })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Split")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    RoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            if (splitAmount.value > 1) {
                                splitAmount.value -= 1
                                if (totalBillState.value != "") {
                                    onValChange(
                                        ((totalBillState.value.trim()
                                            .toDouble() + tip.value) / splitAmount.value)
                                    )
                                }
                            }
                        })
                    Text(
                        text = "${splitAmount.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 10.dp)
                    )
                    RoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = {
                            splitAmount.value += 1
                            if (totalBillState.value != "") {
                                onValChange(
                                    ((totalBillState.value.trim()
                                        .toDouble() + tip.value) / splitAmount.value)
                                )
                            }
                        })
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tip")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "${tip.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$percent%",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Slider(
                    value = tipPercent.value,
                    onValueChange = {
                        tipPercent.value = it
                        if (totalBillState.value != "") {
                            tip.value =
                                (tipPercent.value * totalBillState.value.toFloat()).toInt()
                            onValChange(
                                ((totalBillState.value.trim()
                                    .toDouble() + tip.value) / splitAmount.value)
                            )
                        }
                    },
                    steps = 18
                )
            }
        }
    }
}