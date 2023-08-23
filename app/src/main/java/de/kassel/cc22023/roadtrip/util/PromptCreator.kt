package de.kassel.cc22023.roadtrip.util

fun createRoadtripPrompt(startLocation: String, endLocation: String, startDate: String, endDate: String, transportation: String) : String {
    return "You are a roadtrip planner. Format the response as a json. Plan a trip from " +
            "$startLocation to $endLocation from $startDate to $endDate $transportation. The trip should contain" +
            " a packing list named packing_list as an array of items, the start date as start_date, " +
            "the end date as end_date, the start location name as start_location, the end location name as end_location," +
            " if the trip is possible as is_possible and a list of locations with their name, latitude, longitude, the date you are there, " +
            "a boolean that marks 2-3 must have stops that arent the start or end location " +
            "and possible activities as a list of names, with the field_names name, latitude, longitude, date, must_have,  activities."+
            "Make sure that there are multiple activities for each location. Give one location for each day from $startDate to $endDate. Check that all dates " +
            "are covered and that you arrive on the $endDate at $endLocation. Also check that the distances between locations are below 10 hours"
}