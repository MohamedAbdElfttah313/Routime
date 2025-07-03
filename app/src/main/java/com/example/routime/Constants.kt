package com.example.routime

enum class Emojis(val code : String){
    HAPPY("\uD83D\uDE00") ,
    SAD("\uD83D\uDE1E"),
    ANGRY("\uD83D\uDE20"),
    AWKWARD("\uD83D\uDE05"),
    LOVELY("\uD83D\uDE0D"),
    CRY("\uD83D\uDE22"),
    CONFUSED("\uD83D\uDE15")
}

enum class CategoryEnum(val resId : Int){
    Essential(R.drawable.ic_essential),
    Sleep(R.drawable.ic_sleep),
    Eat(R.drawable.ic_food),
    Family(R.drawable.ic_family),
    Work(R.drawable.ic_work),
    School(R.drawable.ic_school),
    Code(R.drawable.ic_code),
    Friends(R.drawable.ic_friends),
    Doctor(R.drawable.ic_doctor),
    Entertainment(R.drawable.ic_entertainment),
    Watch(R.drawable.ic_watch),
    Study(R.drawable.ic_study),
    Gym(R.drawable.ic_dumbell),
    Gaming(R.drawable.ic_gaming),
    Hobby(R.drawable.ic_hobby),
    Fishing(R.drawable.ic_fish),
    Read(R.drawable.ic_book),
    Chat(R.drawable.ic_chat),
    Social(R.drawable.ic_social)
}

enum class ExtrasNames{
    PARCELABLE_DEED
}

sealed interface Screen{
    data object TodayDeed : Screen
    data object AddDeed : Screen
    data object SearchDeed : Screen
    data object ShowDeed : Screen
}