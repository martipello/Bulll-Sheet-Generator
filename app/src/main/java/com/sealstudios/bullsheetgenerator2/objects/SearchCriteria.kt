package com.sealstudios.bullsheetgenerator2.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchCriteria(
        val title: String,
        val postCode: String,
        val dateFrom: String,
        val dateTo: String,
        val radius: Int
) : Parcelable
