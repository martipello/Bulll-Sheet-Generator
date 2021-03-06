package com.sealstudios.bullsheetgenerator2.viewModels

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sealstudios.bullsheetgenerator2.objects.SearchCriteria
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SearchViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var searchCriteria: MutableLiveData<SearchCriteria> = getSearchCriteriaSavedState()

    var onValidationFailed = MutableLiveData<String>()

    private fun setSearchCriteria(searchCriteria: SearchCriteria) {
        savedStateHandle.set(SEARCH_CRITERIA, searchCriteria)
        this.searchCriteria.value = searchCriteria
    }

    private fun getSearchCriteriaSavedState(): MutableLiveData<SearchCriteria> {
        var searchCriteria = savedStateHandle.get<SearchCriteria>(SEARCH_CRITERIA)
        if (searchCriteria == null){
            val todayString = getDateString()
            val twoDaysLater = getDateString(true)
            searchCriteria = SearchCriteria(title = "", postCode = "", dateFrom = todayString, dateTo = twoDaysLater, radius = 1)
        }
        return MutableLiveData(searchCriteria)
    }

    private fun getDateString(twoDaysLater: Boolean = false): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH)
        val todayString = dateFormat.format(calendar.time)
        if (twoDaysLater){
            calendar.add(Calendar.DAY_OF_MONTH, 2)
            return dateFormat.format(calendar.time)
        }
        return todayString
    }

    fun setRadius(radius: Int) {
        this.searchCriteria.value?.let { setSearchCriteria(it.copy(radius = radius * 5)) }
    }

    fun setJobTitle(title: String){
        this.searchCriteria.value?.let { setSearchCriteria(it.copy(title = title)) }
    }

    fun setPostcode(longitude: Double, latitude: Double, context: Context) {
        val geoCoder = Geocoder(context, Locale.ENGLISH)
        var addresses: List<Address>? = null
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addresses != null && addresses.isNotEmpty()) {
            for (i in addresses.indices) {
                val address = addresses[i]
                if (address.postalCode != null) {
                    val postCode = address.postalCode
                    this.searchCriteria.value?.let { setSearchCriteria(it.copy(postCode = postCode)) }
                    break
                }
            }
        }
    }

    fun validate(): Boolean {
        searchCriteria.value?.let {
            if (it.title.isEmpty()){
                setOnValidationFailed("Please enter a job title")
                return searchCriteria.isValid()
            }
            if (it.postCode.isEmpty()){
                setOnValidationFailed("Please enter a post code")
                return searchCriteria.isValid()
            }
            if (it.dateFrom.isEmpty()){
                setOnValidationFailed("Please enter a date from")
                return searchCriteria.isValid()
            }
            if (it.dateTo.isEmpty()){
                setOnValidationFailed("Please enter a date to")
                return searchCriteria.isValid()
            }
        }
        return searchCriteria.isValid()
    }

    private fun setOnValidationFailed(failMessage: String){
        onValidationFailed.value = failMessage
    }

    companion object {
        const val SEARCH_CRITERIA: String = "searchCriteria"
    }

}

fun MutableLiveData<SearchCriteria>.isValid(): Boolean {
    //TODO check dateTo is in the future
    return !value?.title.isNullOrEmpty()
            && !value?.postCode.isNullOrEmpty()
            && !value?.dateTo.isNullOrEmpty()
            && !value?.dateFrom.isNullOrEmpty()
}