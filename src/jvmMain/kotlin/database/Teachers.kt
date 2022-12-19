package database

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Teachers : IntIdTable() {
    val lastName = text("last_name")
    val name = text("name")
    val patronymic = text("patronymic")
    val gender = enumeration<Gender>("gender")
    val age = uinteger("age")
    val phone = varchar("phone", 18)
}

class Teacher(id: EntityID<Int>) : IntEntity(id) {

    companion object: EntityClass<Int, Teacher>(Teachers)

    var lastName    by Teachers.lastName
    var name        by Teachers.name
    var patronymic  by Teachers.patronymic
    var gender      by Teachers.gender
    var age         by Teachers.age
    var phone       by Teachers.phone

}