package edu.farmingdale.bcs421_bims

import java.io.Serializable

class Users(
    var firstName: String = "",
    var lastName: String = "",
    var dateOfBirth: String = "",
    var email: String = "",
    var password: String = "",
    var orgCode: String = ""
) : Serializable
