/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.udea.compumovil.gr04_20262.lab2.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr04_20262.lab2.data.InboxStore
import kotlinx.coroutines.launch
import co.edu.udea.compumovil.gr04_20262.lab2.data.exampleUiState
import co.edu.udea.compumovil.gr04_20262.lab2.data.initialMessages

/**
 * Holds the conversation state so it survives configuration changes, and handles message
 * actions such as liking a message.
 *
 * @param channel the channel of the conversation
 */
class ConversationViewModel(channel: String) : ViewModel() {

    var uiState by mutableStateOf(
        ConversationUiState(
            channelName = "#$channel",
            channelMembers = exampleUiState.channelMembers,
            initialMessages = initialMessages,
        ),
    )
        private set

    init {
        // Mensajes recibidos por el worker (REST): los existentes y los nuevos en vivo
        val known = HashSet<String>()
        viewModelScope.launch {
            InboxStore.messages.collect { list ->
                list.asReversed().forEach { m ->
                    if (known.add(m.id)) uiState.addMessage(m)
                }
            }
        }
    }

    fun toggleLike(messageId: String) {
        uiState.toggleLike(messageId)
    }
}
