package com.example.tiptime

import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.compose.material3.Switch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import kotlin.math.ceil


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipTimeTheme {
                TipTimeLayout(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}


@Preview
@Composable
fun TipTimePreview(){
    TipTimeLayout(Modifier)
}

@Composable
fun TipTimeLayout(modifier : Modifier){
    var amountInput by remember {
        mutableStateOf("")
    }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    var tipPercent by remember {
        mutableStateOf("")
    }
    val percent = tipPercent.toDoubleOrNull() ?: 0.0
    var roundUp by remember {
        mutableStateOf(false)
    }
    val tip = calculateTip(amount = amount, tipPercent = percent, roundUp = roundUp)
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = stringResource(id = R.string.calculate_tip),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 20.dp, bottom = 15.dp)
            )
        EditNumberField(value = amountInput, onValueChange = {amountInput = it},
            label = R.string.bill_amount,
            icon = R.drawable.money,
            iconDescription = R.string.bill_amount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        EditNumberField(value = tipPercent,
            onValueChange = {tipPercent = it},
            label = R.string.tip_percent,
            icon = R.drawable.percent,
            iconDescription = R.string.tip_percent,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )
        RoundTheTip(roundUp = roundUp, roundUpChange = {roundUp = it}, modifier = Modifier)
        Text(text = stringResource(id = R.string.tip_amount,tip),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.padding(48.dp))
    }
}

@Composable
fun EditNumberField(value : String, onValueChange : (String) -> Unit,
                    @StringRes label : Int, keyboardOptions : KeyboardOptions,
                    @DrawableRes icon : Int, iconDescription : Int,
                    modifier : Modifier){
    TextField(value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = stringResource(id = label)) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        leadingIcon = { Icon(painter = painterResource(id = icon), contentDescription = stringResource(
            id = iconDescription
        ))}
    )
}

@Composable
fun RoundTheTip(roundUp : Boolean, roundUpChange : (Boolean) -> Unit, modifier : Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.round_up_tip
            )
        )
        Switch(checked = roundUp, onCheckedChange = roundUpChange,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.End)
            )
        
    }
}

@VisibleForTesting
internal fun calculateTip(amount : Double, tipPercent : Double, roundUp : Boolean) : String{
    var tip = tipPercent / 100 * amount
    if(roundUp){
        tip = ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}