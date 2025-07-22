package com.example.routime


object Constants {

    //Request Codes
    const val REMINDER_SERVICE_REQUEST_CODE = 101
    const val PENDING_INTENT_REQUEST_CODE = 111

    //Notification Channels
    const val CHANNEL_ID_DEFAULT = "CHANNEL_ID_DEFAULT"
    const val CHANNEL_ID_SOUND = "CHANNEL_ID_SOUND"
    const val CHANNEL_ID_SOUND_VIBRATION = "CHANNEL_ID_SOUND_VIBRATION"

    //Reminder Preferences
    const val SHARED_PREF_NAME = "ROUTIME_SHARED_PREF"
    const val REMINDER_ENABLED = "REMINDER_ENABLED"
    const val REMINDER_FREQUENCY = "REMINDER_FREQUENCY"
    const val REMINDER_START_TIME = "REMINDER_START_TIME"
    const val REMINDER_END_TIME = "REMINDER_END_TIME"
    const val REMINDER_WEEKDAYS_ONLY = "REMINDER_WEEKDAYS_ONLY"
    const val REMINDER_NOTIFICATION_SOUND = "REMINDER_NOTIFICATION_SOUND"
    const val REMINDER_NOTIFICATION_VIBRATE = "REMINDER_NOTIFICATION_VIBRATE"

    //Inactivity Preferences
    const val INACTIVITY_REMINDER_ENABLED = "INACTIVITY_REMINDER_ENABLED"
    const val INACTIVITY_REMINDER_POST_TIME = "INACTIVITY_REMINDER_POST_TIME"
    const val INACTIVITY_REMINDER_MESSAGE = "INACTIVITY_REMINDER_MESSAGE"
    const val INACTIVITY_REMINDER_SOUND_ENABLED = "INACTIVITY_REMINDER_SOUND_ENABLED"

    //External Storage File Paths
    const val SAVED_PICTURES_PATH = "SavedPictures"
    const val SAVED_VIDEOS_PATH = "SavedVideos"
    const val SAVED_DOCS_PATH = "SavedDocuments"
    const val SAVED_AUDIO_PATH = "SavedAudios"

}

enum class FileType{
    Picture,
    Video,
    Document,
    Audio,
    Undefined
}

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
    PARCELABLE_DEED,
    FLAG_SHOULD_SHOW_REMINDER_OVERLAY
}
sealed interface Screen{
    data object TodayDeed : Screen
    data object AddDeed : Screen
    data object SearchDeed : Screen
    data object ShowDeed : Screen
}