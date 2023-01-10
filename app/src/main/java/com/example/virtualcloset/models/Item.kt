package com.example.virtualcloset.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Item(
    val id: String = "",
    val name: String = "",
    val color: String = "",
    val pattern: String = "",
    val Category: String = "",
    val Size: String = "",
    val Style: String = "",
    val image: String = ""
):Parcelable