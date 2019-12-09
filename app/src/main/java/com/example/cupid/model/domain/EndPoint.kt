package com.example.cupid.model.domain

/** Represents a device we can talk to.  */
class Endpoint(val id: String, val name: String) {

    override fun equals(obj: Any?): Boolean {
        if (obj is Endpoint) {
            return id == obj.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return String.format("Endpoint{id=%s, name=%s}", id, name)
    }

}