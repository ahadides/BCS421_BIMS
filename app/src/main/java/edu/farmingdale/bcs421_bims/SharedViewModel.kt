package edu.farmingdale.bcs421_bims

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _dataFromItemEdit = MutableLiveData<Item>()
    val dataFromItemEdit: LiveData<Item> get() = _dataFromItemEdit

    fun updateDataFromItemEdit(data: Item) {
        _dataFromItemEdit.value = data
    }
}