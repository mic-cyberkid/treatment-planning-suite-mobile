package com.mosec.tpsuite

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonSlider(sliderPosition:Float = 1f, onPositionChange: (Float) -> Unit = {println("Slider changed")}){
    Box{
        val interactionSource: MutableInteractionSource =  remember {MutableInteractionSource()}

        Slider(

            thumb = {

                SliderDefaults.Thumb(
                    colors = SliderColors(
                        thumbColor = Color.Red,
                        activeTrackColor = Color.Cyan,
                        activeTickColor = Color.Magenta,
                        inactiveTrackColor = Color.Unspecified,
                        inactiveTickColor = Color.Unspecified,
                        disabledThumbColor = Color.Unspecified,
                        disabledActiveTrackColor = Color.Unspecified,
                        disabledActiveTickColor = Color.Unspecified,
                        disabledInactiveTrackColor = Color.Unspecified,
                        disabledInactiveTickColor = Color.Unspecified
                    ),

                    interactionSource = interactionSource,
                    modifier = Modifier.padding(5.dp).height(15.dp)

                )

            },
            modifier= Modifier.padding(20.dp)
                .background(color=Color.Cyan,
                    shape=RoundedCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 20.dp)),
            value = sliderPosition,
            valueRange = 1f .. 100f,
            onValueChange = {onPositionChange(it)}

        )
    }
}


@Composable
fun SliderUsage(){
    var sliderPosition by remember { mutableStateOf(1f) }

    val handleValueChange = { position:Float ->
        sliderPosition = position
    }

    MonSlider(sliderPosition , handleValueChange)
}

@Composable
fun SliderPreview(){
    Column(modifier = Modifier.background(color=Color(0.18f, 0.714f, 0.325f, 1.0f))
        .padding(20.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(
            modifier= Modifier.
            background(color=Color.White, shape= RoundedCornerShape(20.dp))) {
            Column {
                SliderUsage()
                var checkedState by remember { mutableStateOf(false) }
                Switch(
                    checked = checkedState, onCheckedChange = { checkedState = !checkedState }
                )
            }
        }

    }

}


@Composable
fun MonCard(text: String){
    Column(Modifier.padding(5.dp)
        .fillMaxWidth()
        .height(100.dp)
        .background(color=Color.Red, shape=RoundedCornerShape(5.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(Modifier.safeContentPadding().fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            ){
            Icon(imageVector = Icons.Default.Image,
                contentDescription = "Item Image",
                Modifier.background(Color.White)
                    .height(300.dp)
            )
            Text(text)

            Text("$20.00")
        }

    }

}

@Composable
fun MonLazyList(items : List<String>){
    Column {
        for (item in items){
            MonCard(item)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MonLazyListPreview(){

    MonLazyList(listOf("Mango", "Banana", "Apple", "Tomato"))


}


