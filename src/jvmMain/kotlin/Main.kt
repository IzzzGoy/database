import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import viewmodels.StudentsViewModel
import kotlin.reflect.full.declaredMemberProperties
import database.*
import viewmodels.StudentUpdateModel


@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    Database.connect(
        url = "jdbc:postgresql://127.0.0.1:5432/postgres",
        user = "root",
        password = "root"
    )
    rememberCoroutineScope().launch {
        newSuspendedTransaction(Dispatchers.IO) {
            SchemaUtils.createMissingTablesAndColumns(
                Students, Teachers
            )
        }
    }

    startKoin {
        modules(
            module {
                singleOf(::StudentsViewModel)
            }
        )
    }

    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Cyan)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Students",
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.Blue.copy(alpha = 0.5f)),
                        textAlign = TextAlign.Center
                    )
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    StudentContent()
                }
            }
        }
    }
}

@Composable
fun StudentContent(studentsViewModel: StudentsViewModel = getKoin().get()) {
    val state by studentsViewModel.state.collectAsState()
    state.selectedStudent.fold(
        ifEmpty = {

        },
        ifSome = {
            StudentUpdateDialog(
                isVisible = state.isDialogVisible,
                selectedStudent = it,
                updateLastName = studentsViewModel::updateLastName,
                updateName = studentsViewModel::updateName,
                updatePatronymic = studentsViewModel::updatePatronymic,
                updateGender = studentsViewModel::updateGender,
                updateAge = studentsViewModel::updateAge,
                updateGrade = studentsViewModel::updateGrade,
                save = studentsViewModel::save
                ) {
                studentsViewModel.setDialogVisibility(false)
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(0.3f))
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("id", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("lastName", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("name", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("patronymic", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("gender", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("age", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text("grade", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
            Text(
                "Редактировать",
                modifier = Modifier.weight(1f).border(2.dp, Color.Black),
                textAlign = TextAlign.Center
            )
            Text("Удалить", modifier = Modifier.weight(1f).border(2.dp, Color.Black), textAlign = TextAlign.Center)
        }
        state.students.forEach {
            Row(modifier = Modifier.wrapContentHeight()) {
                Text(
                    it.id.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.lastName,
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.name,
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.patronymic,
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.gender.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.age.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Text(
                    it.grade.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f).animateContentSize(),
                    textAlign = TextAlign.Center
                )
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                        .clickable {
                            studentsViewModel.selectStudent(it.id.value)
                        }
                )
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                        .clickable {
                            studentsViewModel.deleteItem(it.id.value)
                        }
                )
            }
        }
    }
}

@Composable
fun StudentUpdateDialog(
    isVisible: Boolean = false,
    selectedStudent: StudentUpdateModel,
    updateLastName: (String) -> Unit,
    updateName: (String) -> Unit,
    updatePatronymic: (String) -> Unit,
    updateGender: (Gender) -> Unit,
    updateAge: (String) -> Unit,
    updateGrade: (String) -> Unit,
    save: () -> Unit,
    closeDialog: () -> Unit,
) {
    Dialog(
        onCloseRequest = closeDialog,
        visible = isVisible,
    ) {
        Column(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            selectedStudent.let { (
                                         id,
                                         lastName,
                                         name,
                                         patronymic,
                                         gender,
                                         age,
                                         grade
                                     ) ->

                TextField(
                    value = lastName,
                    onValueChange = updateLastName,
                    label = {
                        Text(
                            text = "lastName",
                            textAlign = TextAlign.Center
                        )
                    }
                )
                TextField(
                    value = name,
                    onValueChange = updateName,
                    label = {
                        Text(
                            "name",
                            modifier = Modifier.padding(10.dp).weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                )

                TextField(
                    value = patronymic,
                    onValueChange = updatePatronymic,
                    label = {
                        Text(
                            "patronymic",
                            modifier = Modifier.padding(10.dp).weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                )
                var expanded by remember { mutableStateOf(true) }
                Column {

                    val focusManager = LocalFocusManager.current
                    TextField(
                        value = gender.name,
                        onValueChange = updatePatronymic,
                        readOnly = true,
                        label = {
                            Text(
                                "gender",
                                modifier = Modifier.padding(10.dp).weight(1f),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier.clickable {
                            expanded = true
                        }.onFocusEvent {
                            if (it.isFocused) {
                                expanded = true
                                focusManager.clearFocus()
                            }
                        }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        Gender.values().forEach {
                            DropdownMenuItem(
                                onClick = {
                                    updateGender(it)
                                    expanded = false
                                }
                            ) {
                                Text(it.name)
                            }
                        }
                    }
                }


                TextField(
                    value = age.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f),
                    onValueChange = updateAge,
                    label = {
                        Text("age")
                    },
                )
                TextField(
                    value = grade.toString(),
                    modifier = Modifier.padding(10.dp).weight(1f),
                    onValueChange = updateGrade,
                    label = {
                        Text("grade")
                    },
                )

                TextButton(
                    onClick = save
                ) {
                    Text("Save")
                }
            }
        }
    }
}
