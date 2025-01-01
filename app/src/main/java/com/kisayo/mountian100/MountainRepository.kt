package com.kisayo.mountian100

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize

class MountainRepository(private val context: Context) {
    // Mountain 데이터 클래스
    @Parcelize
    data class Mountain(
        val id: Int,
        val name: String,
        val height: Int,
        val latitude: Double,
        val longitude: Double,
        val address: String
    ) : Parcelable

    // JSON 응답을 위한 래퍼 클래스
    data class MountainsResponse(
        val mountains: List<Mountain>
    )

    // raw 리소스에서 데이터 로드
    fun loadMountainData(): List<Mountain> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.mountainsdata)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<MountainsResponse>() {}.type
            Gson().fromJson<MountainsResponse>(jsonString, type).mountains
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
