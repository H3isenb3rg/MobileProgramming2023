package it.unibs.mp.horace.backend

import java.time.Instant

class TimeEntry {
    var uid: String? = null
    var idActivity: String? = null
    var description: String? = null

    // FIXME: Currently date times are strings we need to find the correct type to use
    var startTime: String? = null
    var endTime: String? = null

    constructor()

    constructor(uid: String?, idActivity: String?, description: String?, startTime: String?, endTime: String?) {
        this.uid = uid
        this.idActivity = idActivity
        this.description = description
        this.startTime = startTime
        this.endTime = endTime
    }
}