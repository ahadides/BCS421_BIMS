package edu.farmingdale.bcs421_bims

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val dataToPass = MutableLiveData<Bundle>()
}
