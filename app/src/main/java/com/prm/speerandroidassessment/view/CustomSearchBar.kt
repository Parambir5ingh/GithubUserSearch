package com.prm.speerandroidassessment.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.daggermvvmdemo.viewmodels.MainViewModel
import com.prm.speerandroidassessment.viewmodels.ProfileViewModel
import kotlinx.coroutines.delay


/*
* This composable is to include the search view on Home screen or anywhere in the project
* */
@Composable
fun CustomSearchBar(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit,
    placeholderText: String = "Search..."
) {

    val focusManager = LocalFocusManager.current

    // Debounce search term to avoid too many API calls
    LaunchedEffect(viewModel.searchQuery) {
        if (viewModel.searchQuery.length > 2) { // Only search if query has at least 3 characters
            delay(500) // Wait for 500ms after typing stops
            onSearch()
        } else if (viewModel.searchQuery.isEmpty()) {
            onSearch() // Clear results if search is empty
        }
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = viewModel.searchQuery,
        onValueChange = { newValue ->
            viewModel.updateQuery(newValue) // Direct assignment works with 'by' delegate
        },
        placeholder = { Text(text = placeholderText) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon"
            )
        },
        trailingIcon = {
            if (viewModel.searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.updateQuery("")
                        onSearch()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        )
    )
}