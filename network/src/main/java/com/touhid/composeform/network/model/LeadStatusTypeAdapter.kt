package com.touhid.composeform.network.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

// Registered on the Gson instance NetworkModule builds (not via @SerializedName on LeadStatus
// itself) - Gson's default enum adapter returns null for a JSON value it doesn't recognize, which
// would then sit inside LeadListItem.status (a non-null Kotlin property) as an unchecked platform
// null. This adapter routes anything unrecognized to LeadStatus.Unknown instead, so a new backend
// status shows up as a real, type-safe enum value rather than a hidden null.
internal object LeadStatusTypeAdapter : TypeAdapter<LeadStatus>() {

    private val toJson = mapOf(
        LeadStatus.Pending to "pending",
        LeadStatus.Approved to "approved",
        LeadStatus.Rejected to "rejected",
    )
    private val fromJson = toJson.entries.associate { (status, value) -> value to status }

    override fun write(out: JsonWriter, value: LeadStatus?) {
        out.value(value?.let { toJson[it] })
    }

    override fun read(reader: JsonReader): LeadStatus {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return LeadStatus.Unknown
        }
        return fromJson[reader.nextString()] ?: LeadStatus.Unknown
    }
}
