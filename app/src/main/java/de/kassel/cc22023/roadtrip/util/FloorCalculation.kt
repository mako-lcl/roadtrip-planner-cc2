package de.kassel.cc22023.roadtrip.util

const val FLOOR_HEIGHT_IN_METERS = 5.0
const val FLOOR_THRESHHOLD = 1.5

fun isOnFloor(currentHeight: Double, baseHeight: Double, floor: Int): Boolean {
    val floorHeight = baseHeight + (floor * FLOOR_HEIGHT_IN_METERS)
    return currentHeight in (floorHeight - FLOOR_THRESHHOLD)..(floorHeight + FLOOR_THRESHHOLD)
}