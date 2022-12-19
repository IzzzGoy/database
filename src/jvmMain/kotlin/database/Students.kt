package database

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Students : IntIdTable() {
    val lastName = text("last_name")
    val name = text("name")
    val patronymic = text("patronymic")
    val gender = enumeration<Gender>("gender")
    val age = uinteger("age")
    val grade = uinteger("grade")
}

enum class Gender {
    MALE, FEMALE, UNKNOWN
}

class Student(id: EntityID<Int>) : IntEntity(id) {

    companion object: EntityClass<Int, Student>(Students)

    var lastName    by Students.lastName
    var name        by Students.name
    var patronymic  by Students.patronymic
    var gender      by Students.gender
    var age         by Students.age
    var grade       by Students.grade

}


