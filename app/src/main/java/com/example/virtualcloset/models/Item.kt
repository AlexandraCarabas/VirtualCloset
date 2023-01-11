package com.example.virtualcloset.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Item(
    val id: String = "",
    val name: String = "",
    val color: String = "",
    val pattern: String = "",
    val category: String = "",
    val size: String = "",
    val style: String = "",
    val image: String = ""
):Parcelable