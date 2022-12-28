package com.example.apptracker.ui.routes.more.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apptracker.R
import com.example.apptracker.ui.components.BackTopAppBar
import com.example.apptracker.ui.components.ResourceText
import com.example.apptracker.ui.components.TextDialog
import com.example.apptracker.ui.components.TextFieldDialog
import com.example.apptracker.util.data.AppDatabase
import com.example.apptracker.util.data.categories.Category

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun CategoriesPage(
    navController: NavController,
    database: AppDatabase,
    viewModel: CategoriesViewModel = CategoriesViewModel(database)
) {
    val screenState by viewModel.state.collectAsState()

    var addCategoryDialogEnabled by remember { mutableStateOf(false) }
    var editNameDialogState by remember { mutableStateOf(CategoryDialogState()) }
    var deleteConfirmDialogEnabled by remember { mutableStateOf(CategoryDialogState()) }

    when {
        addCategoryDialogEnabled -> {
            TextFieldDialog(
                onDismissRequest = { addCategoryDialogEnabled = false },
                titleText = R.string.categories_add_dialog_title,
                confirmText = R.string.categories_add_dialog_confirm,
                dismissText = R.string.categories_add_dialog_dismiss,
                placeholderText = R.string.categories_add_dialog_placeholder,
                labelText = R.string.categories_add_dialog_label,
                onConfirm = {
                    viewModel.addCategory(Category(
                        name = it,
                        position = screenState.categories.count()
                    ))
                    addCategoryDialogEnabled = false
                },
                onDismiss = { addCategoryDialogEnabled = false }
            )
        }
        editNameDialogState.enabled -> {
            TextFieldDialog(
                onDismissRequest = { editNameDialogState = CategoryDialogState() },
                defaultValue = editNameDialogState.category!!.name,
                titleText = R.string.categories_edit_dialog_title,
                confirmText = R.string.categories_edit_dialog_confirm,
                dismissText = R.string.categories_edit_dialog_dismiss,
                placeholderText = R.string.categories_edit_dialog_placeholder,
                labelText = R.string.categories_edit_dialog_label,
                onConfirm = {
                    viewModel.setName(editNameDialogState.category!!, it)
                    editNameDialogState = CategoryDialogState()
                },
                onDismiss = { editNameDialogState = CategoryDialogState() }
            )
        }
        deleteConfirmDialogEnabled.enabled -> {
            val category = deleteConfirmDialogEnabled.category
            TextDialog(
                onDismissRequest = { deleteConfirmDialogEnabled = CategoryDialogState() },
                titleText = R.string.categories_delete_confirm_dialog_title,
                contentText = stringResource(id = R.string.categories_delete_confirm_dialog_content).format(category!!.name),
                confirmText = R.string.categories_delete_confirm_dialog_confirm,
                dismissText = R.string.categories_delete_confirm_dialog_dismiss,
                onConfirm = {
                    viewModel.delete(category)
                    deleteConfirmDialogEnabled = CategoryDialogState()
                },
                onDismiss = { deleteConfirmDialogEnabled = CategoryDialogState() }
            )
        }
    }

    Scaffold (
        topBar = {
            BackTopAppBar(
                title = { Text(stringResource(id = R.string.more_category_button_headline)) },
                onBack = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { addCategoryDialogEnabled = true },
                icon = { Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = stringResource( id = R.string.categories_fab_content_description)
                )},
                text = { Text(stringResource(id = R.string.categories_fab_text)) }
            )
        }
    ) { padding ->
        val categories = screenState.categories
        val length = categories.count()
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .padding(10.dp)
                .fillMaxSize()
        ) {
            if (screenState.categories.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ResourceText(R.string.categories_empty_list_text)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    items(categories, key = { it.id }) {
                        Row(
                            modifier = Modifier.animateItemPlacement()
                        ) {
                            ComposableItem(
                                category = it,
                                isLast = it.position == (length - 1),
                                onMoveUp = { viewModel.setPosition(it, it.position - 1) },
                                onMoveDown = { viewModel.setPosition(it, it.position + 1) },
                                onEdit = {
                                    editNameDialogState = CategoryDialogState(
                                        enabled = true,
                                        category = it
                                    )
                                },
                                onDelete = {
                                    deleteConfirmDialogEnabled = CategoryDialogState(
                                        enabled = true,
                                        category = it
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ComposableItem(
    category: Category,
    isLast: Boolean = false,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(end = 10.dp),
                    painter = painterResource(id = R.drawable.label_icon),
                    contentDescription = category.name
                )
                Text(
                    text = category.name
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(.5f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        enabled = category.position != 0,
                        onClick = onMoveUp
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.up_icon),
                            contentDescription = stringResource(id = R.string.categories_move_up_button_content_description)
                        )
                    }
                    IconButton(
                        enabled = !isLast,
                        onClick = onMoveDown
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.down_icon),
                            contentDescription = stringResource(id = R.string.categories_move_down_button_content_description)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_icon),
                            contentDescription = stringResource(id = R.string.categories_edit_button_content_description)
                        )
                    }
                    IconButton(
                        onClick = onDelete
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = stringResource(id = R.string.categories_delete_button_content_description)
                        )
                    }
                }
            }
        }
    }
}

