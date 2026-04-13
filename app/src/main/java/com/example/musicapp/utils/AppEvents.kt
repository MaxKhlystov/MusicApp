package com.example.musicapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AppEvents {
    private val _updateSearchResults = MutableLiveData<Unit>()
    val updateSearchResults: LiveData<Unit> = _updateSearchResults
    fun notifyUpdateSearchResults() {
        _updateSearchResults.postValue(Unit)
    }
}