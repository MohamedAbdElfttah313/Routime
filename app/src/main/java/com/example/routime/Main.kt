package com.example.routime

import com.example.routime.Data.Models.Deed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

fun main(vararg args: String) {
    runBlocking {
        val uiState = MutableStateFlow<UiState>(UiState.Loading)
        val person = People("ahmed", "mohamed", "0123123" , "ny,231,cs", good = "Not that good")

        println(person)

    }
}

data class People(
    var firstName : String,
    var lastName : String,
    var phoneNumber : String,
    var address : String,
    val good : String = "Good",
    val employed : String = "Unemployed"
)



sealed interface UiState {
    data object Loading : UiState
    data class Data(val deed : Deed) : UiState
    data class Error(val error : RoutedError) : UiState
}

enum class RoutedError(val code : Int){
    BAD_NETWORK(400),
    NO_INTERNET(401),
    NOT_FOUND_DEED(402),
    EMPTY_DAY(403)
}