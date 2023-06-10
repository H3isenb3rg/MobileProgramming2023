package it.unibs.mp.horace.backend

class Area {
    var uid: String? = null
    var name: String? = null

    constructor()

    constructor(id: String?, name: String?) {
        this.uid = id
        this.name = name
    }
}