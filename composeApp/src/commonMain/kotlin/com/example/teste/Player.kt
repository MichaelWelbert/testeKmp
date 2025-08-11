package com.example.teste

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.rufenkhokhar.KMPPlayer
import io.github.rufenkhokhar.KMPPlayerEvent
import io.github.rufenkhokhar.KMPPlayerState
import io.github.rufenkhokhar.rememberKMPPlayerState

@OptIn(ExperimentalMultiplatform::class)
@Composable
fun Player() {
    val playerState = rememberKMPPlayerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        KMPPlayer(
            state = playerState,
            showControls = true,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )


        PlayerControls(playerState = playerState)
    }

    LaunchedEffect(Unit) {
        // Usando arquivo da pasta raw com protocolo correto
        playerState.setFileUrl("android.resource://com.example.teste/raw/sample_short_single_channel_m4a")
        playerState.play()

        playerState.observePlayerEvent().collect { event ->
            when (event) {
                KMPPlayerEvent.Buffering -> println("kmp_state: Buffering")
                KMPPlayerEvent.Ended     -> println("kmp_state: Ended")
                is KMPPlayerEvent.Error  -> println("kmp_state: Error: ${event.message}")
                KMPPlayerEvent.Ideal     -> println("kmp_state: Ideal")
                KMPPlayerEvent.Playing   -> println("kmp_state: Playing")
                KMPPlayerEvent.Stop      -> println("kmp_state: Stop")
                KMPPlayerEvent.Paused    -> println("kmp_state: Paused")
            }
        }
    }
}

@OptIn(ExperimentalMultiplatform::class)
@Composable
fun PlayerControls(playerState: KMPPlayerState) {
    var isPlaying by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(false) }
    
    // Lista de arquivos disponíveis na pasta raw
    val availableFiles = listOf(
        "sample_short_single_channel_m4a",
        "sample_short_single_channel_mp3", 
        "testmp4"
    )
    
    var currentFileIndex by remember { mutableStateOf(0) }
    var currentFile by remember { mutableStateOf(availableFiles[0]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Controles do Player",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de controle principal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    playerState.play()
                    isPlaying = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("▶️ Play")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    playerState.pause()
                    isPlaying = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("⏸️ Pause")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    playerState.stop()
                    isPlaying = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("⏹️ Stop")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de navegação (Previous e Next)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    // Anterior
                    currentFileIndex = if (currentFileIndex > 0) currentFileIndex - 1 else availableFiles.size - 1
                    currentFile = availableFiles[currentFileIndex]
                    val filePath = "android.resource://com.example.teste/raw/$currentFile"
                    playerState.setFileUrl(filePath)
                    playerState.play()
                    isPlaying = true
                    println("Carregando arquivo anterior: $filePath")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("⏮️ Previous")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Próximo
                    currentFileIndex = (currentFileIndex + 1) % availableFiles.size
                    currentFile = availableFiles[currentFileIndex]
                    val filePath = "android.resource://com.example.teste/raw/$currentFile"
                    playerState.setFileUrl(filePath)
                    playerState.play()
                    isPlaying = true
                    println("Carregando próximo arquivo: $filePath")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("⏭️ Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de configuração
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    isLooping = !isLooping
                    playerState.setVideoLoop(isLooping)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isLooping) "🔄 Loop ON" else "🔄 Loop OFF")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Carregar arquivo específico
                    val filePath = "android.resource://com.example.teste/raw/$currentFile"
                    playerState.setFileUrl(filePath)
                    println("Carregando arquivo: $filePath")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📁 Carregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de status e destruição
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val playing = playerState.isPlaying()
                    println("Status do player: ${if (playing) "Reproduzindo" else "Parado"}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📊 Status")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    playerState.destroy()
                    isPlaying = false
                    isLooping = false
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🗑️ Destroy")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status atual
        Text(
            text = "Status: ${if (isPlaying) "Reproduzindo" else "Parado"} | Loop: ${if (isLooping) "ON" else "OFF"}",
            color = Color.White,
            fontSize = 14.sp
        )
        
        Text(
            text = "Arquivo atual: $currentFile (${currentFileIndex + 1}/${availableFiles.size})",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
