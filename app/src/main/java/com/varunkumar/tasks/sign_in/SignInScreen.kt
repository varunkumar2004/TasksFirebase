package com.varunkumar.tasks.sign_in

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.varunkumar.tasks.R
import com.varunkumar.tasks.viewmodels.SignInState

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White),
            painter = painterResource(id = R.drawable.task_img),
            contentDescription = "Logo",
            colorFilter = ColorFilter.tint(
                color = Color.Transparent,
                blendMode = BlendMode.Difference
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onSignInClick) {
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 25.sp
                    )
                ) {
                    append("Sign in with ")
                }

                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Google")
                }
            }

            Text(text = annotatedString)
        }
    }
}