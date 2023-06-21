package com.example.virtualcloset.utils

object Constants {
    const val USERS: String = "users"
    const val VIRTUALCLOSET_PREFERENCES: String = "VirtualClosetPrefs"
    const val SIGNED_IN_USERNAME: String = "signed_in_username"
    const val SIGNED_IN_UID : String = "signed_in_uid"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val HOME: String = "home"
    const val CLOSET: String = "closet"

    const val ITEMS: String = "items"
    const val OUTFITS: String = "outfits"
    const val CATEGORY: String ="category"
    const val ITEM_NAME: String = "name"
    const val ITEM_COLOR: String = "color"
    const val ITEM_PATTERN: String = "pattern"
    const val ITEM_CATEGORY: String = "category"
    const val ITEM_SIZE: String = "size"
    const val ITEM_STYLE: String = "style"
    const val ITEM_IMAGE: String = "image"
    const val OUTFIT_NAME : String = "name"
    const val OUTFIT_CATEGORY :String = "name"
    const val OUTFIT_ID: String = "id"
    const val OUTFIT_STYLE: String = "style"
    const val OUTFIT_IMAGES: String = "images"
    const val OUTFIT_ITEMS: String = "items"

    const val CAMERA_PERMISSIONS_CODE = 1
    const val CAMERA_REQUEST_CODE = 2
    const val READ_STORAGE_PERMISSION_CODE = 3
    const val PICK_IMAGE_REQUEST_CODE = 4
    const val WRITE_STORAGE_PERMISSION_CODE = 5

    val category_options = arrayOf<String>(
        "Tops",
        "Bottoms",
        "Dresses",
        "Accessories",
        "Bags",
        "Shoes")

    val outfit_category_options = arrayOf<String>(
        "AllSeasons",
        "Spring",
        "Summer",
        "Fall",
        "Winter"
    )

    val style_options = arrayOf<String>(
        "Elegant",
        "Business",
        "Casual",
        "Sport",
        "Beach",
        "Retro",
        "Vintage"
    )
}