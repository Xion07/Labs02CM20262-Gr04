/*
 * Copyright 2020 The Android Open Source Project
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

package co.edu.udea.compumovil.gr04_20262.lab2.profile

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr04_20262.lab2.data.remote.ApiClient
import kotlinx.coroutines.launch
import co.edu.udea.compumovil.gr04_20262.lab2.data.colleagueProfile
import co.edu.udea.compumovil.gr04_20262.lab2.data.meProfile

class ProfileViewModel : ViewModel() {

    private var userId: String = ""

    fun setUserId(newUserId: String?) {
        if (newUserId != userId) {
            userId = newUserId ?: meProfile.userId
        }
        // Workaround for simplicity
        _userData.value = if (userId == meProfile.userId || userId == meProfile.displayName) {
            meProfile
        } else {
            colleagueProfile
        }
        loadRemoteData()
    }

    /** Complementa el perfil con datos del servicio REST (Retrofit). */
    private fun loadRemoteData() {
        val remoteId = if (userId == meProfile.userId || userId == meProfile.displayName) 2 else 1
        viewModelScope.launch {
            runCatching { ApiClient.api.user(remoteId) }.onSuccess { u ->
                val current = _userData.value ?: return@onSuccess
                // Datos complementarios del servicio REST; los del ejemplo se conservan
                _userData.value = current.copy(
                    remoteJob = u.company.title,
                    remoteCompany = u.company.name,
                    remoteEmail = u.email,
                    remoteUsername = u.username,
                )
            }
        }
    }

    private val _userData = MutableLiveData<ProfileScreenState>()
    val userData: LiveData<ProfileScreenState> = _userData
}

@Immutable
data class ProfileScreenState(
    val userId: String,
    @param:DrawableRes val photo: Int?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?, // Null if me
    val commonChannels: String?, // Null if me
    // Lab 2: complementados con https://dummyjson.com/users/{id}
    val remoteJob: String = "",
    val remoteCompany: String = "",
    val remoteEmail: String = "",
    val remoteUsername: String = "",
) {
    fun isMe() = userId == meProfile.userId
}
