package de.kassel.cc22023.roadtrip.util

fun createRoadtripPrompt(startLocation: String, endLocation: String, startDate: String, endDate: String, transportation: String) : String {
    return "You are a roadtrip planner. Format the response as a json. Plan a trip from " +
            "$startLocation to $endLocation from $startDate to $endDate $transportation. The trip should contain" +
            " a packing list named packing_list as an array of items, the start date as start_date, " +
            "the end date as end_date, the start location name as start_location, the end loation name as end_location," +
            " if the trip is possible as is_possible and a list of locations with their name, latitude, longitude and possible" +
            " activites as a list of names. Reduce the number of stops to a maximum of 10."
}