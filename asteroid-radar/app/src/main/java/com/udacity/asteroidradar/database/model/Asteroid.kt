package com.udacity.asteroidradar.database.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "asteroids")
@Parcelize
data class Asteroid(
    @PrimaryKey val id: Long,
    val codename: String,
    @ColumnInfo(name = "close_approach_date") val closeApproachDate: String,
    @ColumnInfo(name = "absolute_magnitude") val absoluteMagnitude: Double,
    @ColumnInfo(name = "estimated_diameter") val estimatedDiameter: Double,
    @ColumnInfo(name = "relative_velocity") val relativeVelocity: Double,
    @ColumnInfo(name = "distance_from_earth") val distanceFromEarth: Double,
    @ColumnInfo(name = "is_potentially_hazardous") val isPotentiallyHazardous: Boolean) : Parcelable