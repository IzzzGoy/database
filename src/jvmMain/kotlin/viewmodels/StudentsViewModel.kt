package viewmodels

import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import database.Gender
import database.Student
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


data class StudentsState(
    val students: List<Student> = emptyList(),
    val isDialogVisible: Boolean = false,
    val selectedStudent: Option<StudentUpdateModel> = none()
)


data class StudentUpdateModel(
    val id: Int,
    val lastName: String,
    val name: String,
    val patronymic: String,
    val gender: Gender,
    val age: Int,
    val grade: Int,
)

class StudentsViewModel {
    @OptIn(DelicateCoroutinesApi::class)
    private val scope = CoroutineScope(newSingleThreadContext(this::class.simpleName.orEmpty()))

    private val _state = MutableStateFlow(StudentsState())
    val state = _state.asStateFlow()

    init {
        scope.launch {
            delay(1.seconds.toLong(DurationUnit.MILLISECONDS))
            _state.update {
                it.copy(
                    students = newSuspendedTransaction {
                        Student.all().toList()
                    }
                )
            }
        }
    }

    fun setDialogVisibility(isDialogVisible: Boolean) = scope.launch {
        _state.update {
            it.copy(isDialogVisible = isDialogVisible)
        }
    }

    fun deleteItem(itemId: Int) = scope.launch {

        _state.update {
            it.copy(
                students = newSuspendedTransaction {
                    Student.findById(itemId)?.delete()
                    Student.all().toList()
                }
            )
        }
    }

    fun selectStudent(studentId: Int) = scope.launch {
        _state.update {
            it.copy(
                selectedStudent = newSuspendedTransaction {
                    Student.findById(studentId)?.let { student ->
                        StudentUpdateModel(
                            student.id.value,
                            student.lastName,
                            student.name,
                            student.patronymic,
                            student.gender,
                            student.age.toInt(),
                            student.grade.toInt()
                        )
                    }.toOption()
                }
            )
        }
        setDialogVisibility(true)
    }

    fun updateLastName(lastName: String) {
        scope.launch {
            _state.update {
                it.copy(selectedStudent = it.selectedStudent.map { it.copy(lastName = lastName) })
            }
        }
    }

    fun updateName(name: String) {
        scope.launch {
            _state.update {
                it.copy(selectedStudent = it.selectedStudent.map { it.copy(name = name) })
            }
        }
    }

    fun updatePatronymic(patronymic: String) {
        scope.launch {
            _state.update {
                it.copy(
                    selectedStudent = it.selectedStudent.map { it.copy(patronymic = patronymic) }
                )
            }
        }
    }

    fun updateGender(gender: Gender) {
        scope.launch {
            _state.update {
                it.copy(
                    selectedStudent = it.selectedStudent.map { it.copy(gender = gender) }
                )
            }
        }
    }

    fun save() {
        scope.launch {
            _state.update { value ->
                value.selectedStudent.fold(
                    ifEmpty = { value.copy() },
                    ifSome = { student ->
                        newSuspendedTransaction {
                            Student.findById(student.id)?.also {
                                it.grade = student.grade.toUInt()
                                it.name = student.name
                                it.patronymic = student.patronymic
                                it.lastName = student.lastName
                                it.age = student.age.toUInt()
                                it.gender = student.gender
                            }
                        }
                        value.copy(
                            students = newSuspendedTransaction { Student.all().toList() },
                            isDialogVisible = false
                        )
                    }
                )

            }
        }
    }

    fun updateAge(age: String) {
        scope.launch {
            age.toIntOrNull()?.also { newAge ->
                _state.update {
                    it.copy(
                        selectedStudent = it.selectedStudent.map { it.copy(age = newAge) }
                    )
                }
            }
        }
    }

    fun updateGrade(grade: String) {
        scope.launch {
            grade.toIntOrNull()?.also { newgrade ->
                _state.update {
                    it.copy(
                        selectedStudent = it.selectedStudent.map { it.copy(grade = newgrade) }
                    )
                }
            }
        }
    }

}