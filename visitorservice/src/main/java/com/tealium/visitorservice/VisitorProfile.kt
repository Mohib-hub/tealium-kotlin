package com.tealium.visitorservice

import com.tealium.core.JsonUtils
import org.json.JSONArray
import org.json.JSONObject

const val KEY_AUDIENCES = "audiences"
const val KEY_BADGES = "badges"
const val KEY_DATES = "dates"
const val KEY_FLAGS = "flags"
const val KEY_FLAG_LISTS = "flag_lists"
const val KEY_METRICS = "metrics"
const val KEY_METRIC_LISTS = "metric_lists"
const val KEY_METRIC_SETS = "metric_sets"
const val KEY_PROPERTIES = "properties"
const val KEY_PROPERTY_LISTS = "property_lists"
const val KEY_PROPERTY_SETS = "property_sets"
const val KEY_CURRENT_VISIT = "current_visit"
const val KEY_CREATED_AT = "creation_ts"
const val KEY_TOTAL_EVENT_COUNT = "total_event_count"
const val KEY_TOTAL_EVENT_COUNT_METRIC = "22"

/**
 * Holds all visitor-scoped attribute data relating to the current visitor identified by the [Tealium.visitorId].
 * Visit-scoped attribute data can be accessed via the [currentVisit] property.
 */
data class VisitorProfile(
        var audiences: Map<String, String>? = null,
        var badges: Map<String, Boolean>? = null,
        var dates: Map<String, Long>? = null,
        var booleans: Map<String, Boolean>? = null,
        var arraysOfBooleans: Map<String, List<Boolean>>? = null,
        var numbers: Map<String, Double>? = null,
        var arraysOfNumbers: Map<String, List<Double>>? = null,
        var tallies: Map<String, Map<String, Double>>? = null,
        var strings: Map<String, String>? = null,
        var arraysOfStrings: Map<String, List<String>>? = null,
        var setsOfStrings: Map<String, Set<String>>? = null,
        var totalEventCount: Int = 0,
        var currentVisit: CurrentVisit? = null) {

    companion object {
        fun fromJson(json: JSONObject): VisitorProfile {
            val visitorProfile = VisitorProfile()

            json.optJSONObject(KEY_AUDIENCES)?.let {
                visitorProfile.audiences = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to entry.value as String
                        }
            }

            json.optJSONObject(KEY_BADGES)?.let {
                visitorProfile.badges = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to entry.value as Boolean
                        }
            }

            json.optJSONObject(KEY_DATES)?.let {
                visitorProfile.dates = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to entry.value as Long
                        }
            }

            json.optJSONObject(KEY_FLAGS)?.let {
                visitorProfile.booleans = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to entry.value as Boolean
                        }
            }

            json.optJSONObject(KEY_FLAG_LISTS)?.let {
                visitorProfile.arraysOfBooleans = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            val values = entry.value as JSONArray
                            val booleans = ArrayList<Boolean>()
                            for (i in 0 until values.length()) {
                                booleans.add(values.get(i) as Boolean)
                            }
                            entry.key to booleans
                        }
            }

            json.optJSONObject(KEY_METRICS)?.let {
                visitorProfile.numbers = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to toDouble(entry.value)
                        }.also { metrics ->
                            if (metrics.containsKey(KEY_TOTAL_EVENT_COUNT_METRIC)) {
                                visitorProfile.totalEventCount = metrics.getValue(KEY_TOTAL_EVENT_COUNT_METRIC).toInt()
                            }
                        }
            }

            json.optJSONObject(KEY_METRIC_LISTS)?.let {
                visitorProfile.arraysOfNumbers = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            val values = entry.value as JSONArray
                            val doubles = ArrayList<Double>()
                            for (i in 0 until values.length()) {
                                doubles.add(toDouble(values.get(i)))
                            }
                            entry.key to doubles
                        }
            }

            json.optJSONObject(KEY_METRIC_SETS)?.let {
                visitorProfile.tallies = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            val values = entry.value as JSONObject
                            val tallyValues = JsonUtils.mapFor(values)
                                    .entries
                                    .associate { tally ->
                                        tally.key to toDouble(tally.value)
                                    }
                            entry.key to tallyValues
                        }
            }

            json.optJSONObject(KEY_PROPERTIES)?.let {
                visitorProfile.strings = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            entry.key to entry.value as String
                        }
            }

            json.optJSONObject(KEY_PROPERTY_LISTS)?.let {
                visitorProfile.arraysOfStrings = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            val values = entry.value as JSONArray
                            val strings = ArrayList<String>()
                            for (i in 0 until values.length()) {
                                strings.add(values.get(i) as String)
                            }
                            entry.key to strings
                        }
            }

            json.optJSONObject(KEY_PROPERTY_SETS)?.let {
                visitorProfile.setsOfStrings = JsonUtils.mapFor(it)
                        .entries
                        .associate { entry ->
                            val values = entry.value as JSONArray
                            val strings = mutableSetOf<String>()
                            for (i in 0 until values.length()) {
                                strings.add(values.get(i) as String)
                            }
                            entry.key to strings
                        }
            }

            json.optJSONObject(KEY_CURRENT_VISIT)?.let {
                visitorProfile.currentVisit = CurrentVisit.fromJson(it)
            }

            return visitorProfile
        }

        fun toDouble(value: Any): Double {
            return when (value as? Int) {
                null -> value as Double
                else -> value.toDouble()
            }
        }
    }
}