package com.example.virtualcloset.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Outfit (
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val style: String = "",
    val images: ArrayList<String> = arrayListOf(),
    val items: ArrayList<Item> = arrayListOf()
): Parcelable