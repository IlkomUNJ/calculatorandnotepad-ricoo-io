package com.example.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object TextEditorScreen {
    private val notes = mutableStateListOf<Note>()
    private var noteCounter = 0

    data class Note(
        val id: Int,
        var title: String,
        var content: String,
        var fontSize: Float = 16f,
        var isBold: Boolean = false,
        var isItalic: Boolean = false
    )

    @Composable
    fun SetupLayout(navController: NavController) {
        val innerNavController = rememberNavController()

        NavHost(
            navController = innerNavController,
            startDestination = "notepad_menu"
        ) {
            composable("notepad_menu") {
                NotepadMenu(innerNavController = innerNavController, outerNavController = navController)
            }
            composable("text_editor/{noteId}") { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                TextEditorCanvas(innerNavController = innerNavController, noteId = noteId)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NotepadMenu(
        innerNavController: NavHostController,
        outerNavController: NavController
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        Text("Notepad")
                    },
                    navigationIcon = {
                        IconButton(onClick = { outerNavController.popBackStack() }) {
                            Icon(
                                modifier = Modifier.graphicsLayer(scaleX = -1f),
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Back to Main Menu"
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val newNote = Note(
                            id = ++noteCounter,
                            title = "Note $noteCounter",
                            content = "",
                            fontSize = 16f,
                            isBold = false,
                            isItalic = false
                        )
                        notes.add(newNote)
                        innerNavController.navigate("text_editor/${newNote.id}")
                    }
                ) {
                    Icon(Icons.Filled.Add, "Add new note")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Your Notes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(12.dp))

                if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No notes yet.\nTap '+' to create one!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notes.forEach { note ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                innerNavController.navigate("text_editor/${note.id}")
                                            }
                                    ) {
                                        Text(
                                            note.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            note.content.ifEmpty { "No content yet" },
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { notes.remove(note) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Note"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextEditorCanvas(innerNavController: NavHostController, noteId: Int?) {
        val note = notes.find { it.id == noteId } ?: return

        var textSize by remember { mutableStateOf(note.fontSize) }
        var textStyle by remember {
            mutableStateOf(
                TextStyle(
                    fontSize = note.fontSize.sp,
                    fontWeight = if (note.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (note.isItalic) FontStyle.Italic else FontStyle.Normal
                )
            )
        }

        var textState by remember { mutableStateOf(TextFieldValue(note.content)) }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = { Text(note.title) },
                    navigationIcon = {
                        IconButton(onClick = { innerNavController.popBackStack() }) {
                            Icon(
                                modifier = Modifier.graphicsLayer(scaleX = -1f),
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            notes.remove(note)
                            innerNavController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note",
                            )
                        }

                        IconButton(onClick = {
                            val index = notes.indexOfFirst { it.id == note.id }
                            if (index >= 0) {
                                notes[index] = note.copy(
                                    content = textState.text,
                                    fontSize = textSize,
                                    isBold = textStyle.fontWeight == FontWeight.Bold,
                                    isItalic = textStyle.fontStyle == FontStyle.Italic
                                )
                            }
                            innerNavController.popBackStack()
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save Note")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    tonalElevation = 26.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .shadow(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isBold = textStyle.fontWeight == FontWeight.Bold
                        IconButton(
                            onClick = {
                                textStyle = if (isBold)
                                    textStyle.copy(fontWeight = FontWeight.Normal)
                                else
                                    textStyle.copy(fontWeight = FontWeight.Bold)
                            },
                            modifier = Modifier
                                .background(
                                    if (isBold)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else
                                        Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = "Bold",
                                tint = if (isBold)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val isItalic = textStyle.fontStyle == FontStyle.Italic
                        IconButton(
                            onClick = {
                                textStyle = if (isItalic)
                                    textStyle.copy(fontStyle = FontStyle.Normal)
                                else
                                    textStyle.copy(fontStyle = FontStyle.Italic)
                            },
                            modifier = Modifier
                                .background(
                                    if (isItalic)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else
                                        Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatItalic,
                                contentDescription = "Italic",
                                tint = if (isItalic)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                textSize = (textSize - 2).coerceAtLeast(8f)
                                textStyle = textStyle.copy(fontSize = textSize.sp)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease Text Size",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                textSize += 2
                                textStyle = textStyle.copy(fontSize = textSize.sp)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase Text Size",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                    placeholder = {
                        Text(
                            "Start typing...",
                            style = textStyle.copy(color = Color.Gray)
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
