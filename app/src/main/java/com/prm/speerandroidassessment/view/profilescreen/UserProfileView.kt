package com.example.daggerhiltdemo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.prm.speerandroidassessment.models.GitUserModel
import com.prm.speerandroidassessment.models.UserProfileModel
import com.prm.speerandroidassessment.view.UserRow
import com.prm.speerandroidassessment.viewmodels.ProfileViewModel

/*
* This composable is to show user profile and his/her followers and followings
* */
@Preview
@Composable
fun UserProfilePreview() {
    UserProfileView(remember { mutableStateOf(false) }, "", {}) { }
}

@Composable
fun UserProfileView(
    darkThemeState: MutableState<Boolean>,
    username: String,
    onUserSelected: (username: String) -> Unit,
    onDismiss: () -> Unit
) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val followers: State<List<GitUserModel>> = viewModel.followersLiveData?.collectAsState()!!
    val followings: State<List<GitUserModel>> = viewModel.followingsLiveData?.collectAsState()!!

    LaunchedEffect(username) {
        viewModel.getUserProfile(username)
    }

    val userModel: State<UserProfileModel?>? = viewModel.userProfile?.collectAsState()

    // State to manage which user's details to show
    var viewPhotoDialog = remember { mutableStateOf(false) }

    // Show dialog if a user is selected
    if (viewPhotoDialog.value && userModel != null) {
        UserPhotoDialog(
            onDismiss = { viewPhotoDialog.value = false }, userModel?.value?.avatar_url!!
        )
    }

    userModel?.value?.let {
        val tabTitles = listOf("Followers(${it.followers})", "Following(${it.following})")
        var selectedTabIndex = remember { mutableStateOf(0) }

        MainContent(
            userModel,
            darkThemeState,
            onDismiss,
            viewPhotoDialog,
            selectedTabIndex,
            tabTitles,
            viewModel,
            followers,
            onUserSelected,
            followings
        )
    }
}

@Composable
private fun MainContent(
    userModel: State<UserProfileModel?>?,
    darkThemeState: MutableState<Boolean>,
    onDismiss: () -> Unit,
    viewPhotoDialog: MutableState<Boolean>,
    selectedTabIndex: MutableState<Int>,
    tabTitles: List<String>,
    viewModel: ProfileViewModel,
    followers: State<List<GitUserModel>>,
    onUserSelected: (username: String) -> Unit,
    followings: State<List<GitUserModel>>
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (userModel?.value != null) {
            CustomTopAppBar(userModel.value?.name, true, darkThemeState) {
                onDismiss()
            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = userModel?.value?.avatar_url,
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                viewPhotoDialog.value = true
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userModel?.value?.name ?: "Unknown",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Username: ${userModel?.value?.login}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        userModel?.value?.location?.takeIf { it.isNotBlank() }?.let { location ->
                            Text(
                                text = "Location: $location",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Show company only if not null/empty
                        userModel?.value?.company?.takeIf { it.isNotBlank() }?.let { company ->
                            Text(
                                text = "Company: $company",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        UserInfoItem("GitHub Profile", userModel?.value?.html_url)

                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    modifier = Modifier.fillMaxWidth(),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = { selectedTabIndex.value = index },
                            text = { Text(text = title) }
                        )
                    }
                }

                // Tab Content
                when (selectedTabIndex.value) {
                    0 -> FollowersList(selectedTabIndex.value, viewModel, followers, onUserSelected)
                    1 -> FollowingList(
                        selectedTabIndex.value,
                        viewModel,
                        followings,
                        onUserSelected
                    )
                }
            }
        }
    }
}

@Composable
fun FollowersList(
    selectedTabIndex: Int,
    viewModel: ProfileViewModel,
    followers: State<List<GitUserModel>>,
    onUserSelected: (username: String) -> Unit,
) {
    LaunchedEffect(selectedTabIndex) {
        viewModel.getFollowersList()
    }

    if (followers.value.isEmpty()) {
        EmptyState(message = "No followers yet")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(followers.value) { user ->
                UserRow(user, {
                    onUserSelected(user.login)
                })
            }
        }
    }
}

@Composable
fun FollowingList(
    selectedTabIndex: Int,
    viewModel: ProfileViewModel,
    following: State<List<GitUserModel>>,
    onUserSelected: (username: String) -> Unit,
) {
    LaunchedEffect(selectedTabIndex) {
        viewModel.getFollowingsList()
    }

    if (following.value.isEmpty()) {
        EmptyState(message = "Not following anyone yet")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(following.value) { user ->
                UserRow(user, {
                    onUserSelected(user.login)
                })
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun UserInfoItem(label: String, value: String?) {
    if (!value.isNullOrEmpty()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}