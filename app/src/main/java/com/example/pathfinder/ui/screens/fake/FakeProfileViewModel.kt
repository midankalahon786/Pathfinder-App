package com.example.pathfinder.ui.screens.fake

import com.example.pathfinder.graphql.type.Gender
import com.example.pathfinder.viewmodel.IProfileViewModel
import com.example.pathfinder.viewmodel.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeProfileViewModel : IProfileViewModel {
    override val name: StateFlow<String> = MutableStateFlow("James Harried (Preview)")
    override val phone: StateFlow<String> = MutableStateFlow("123-456-7890")
    override val birthday: StateFlow<String> = MutableStateFlow("01/01/2000")
    override val email: StateFlow<String> = MutableStateFlow("james.preview@email.com")
    override val uiState: StateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState.Idle)
    override val gender: StateFlow<Gender?> = MutableStateFlow(Gender.MALE)
    override val profileImageUrl: StateFlow<String?> = MutableStateFlow(null)



    override fun onGenderChange(newGender: Gender) {}
    override fun onNameChange(newName: String) {}
    override fun onPhoneChange(newPhone: String) {}
    override fun onBirthdayChange(newBirthday: String) {}
    override fun fetchProfile() {}
    override fun saveChanges() {}
    override fun resetUiState() {}
}