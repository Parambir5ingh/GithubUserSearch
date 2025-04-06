package com.example.daggerhiltdemo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daggermvvmdemo.viewmodels.MainViewModel
import com.prm.speerandroidassessment.R
import com.prm.speerandroidassessment.models.GitUserModel
import com.prm.speerandroidassessment.view.CustomSearchBar
import com.prm.speerandroidassessment.view.UserRow
import kotlinx.coroutines.launch

/*
* Created by Parambir Singh ON 2025-04-06
*/

@Preview
@Composable
fun HomePreview() {
    HomeView(viewModel(), remember { mutableStateOf(false) }, {})
}

@Composable
fun HomeView(
    viewmodel: MainViewModel,
    darkThemeState: MutableState<Boolean>,
    onItemClick: (selectedUser: String) -> Unit
) {
    val scope = rememberCoroutineScope()

    val users: State<List<GitUserModel>> = viewmodel.userLiveData?.collectAsState()!!
    val showProgressIndicator: State<Boolean> = viewmodel._progress.observeAsState(true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CustomTopAppBar(stringResource(R.string.speer_assessment_proj), false, darkThemeState) {

            }

            CustomSearchBar(viewmodel, modifier = Modifier.fillMaxWidth(), {
                scope.launch {
                    viewmodel.searchGitUsers()
                }
            }, "Search")

            Box(modifier = Modifier.fillMaxSize()) {
                if (users.value.isEmpty()) {
                    // Show empty state message
                    Text(
                        text = "No users found",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    // Show the list when there are items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(users.value) { user ->
                            UserRow(user, {
                                scope.launch {
                                    onItemClick(user.login)
                                }
                            })
                        }
                    }
                }
            }

        }
        showProgressIndicator.value.let {
            if (it) CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 8.dp
            )
        }
    }
}