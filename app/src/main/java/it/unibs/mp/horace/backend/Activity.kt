package it.unibs.mp.horace.backend

class Activity {
    var uid: String? = null
    var name: String? = null
    var areaId: String? = null

    constructor()

    constructor(id: String?, name: String?, areaId: String?){
        this.uid = id
        this.name = name
        this.areaId = areaId
    }
}