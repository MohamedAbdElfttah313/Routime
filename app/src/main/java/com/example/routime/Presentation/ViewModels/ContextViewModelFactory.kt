import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.routime.Presentation.ViewModels.AddNewDeedViewModel
import com.example.routime.Presentation.ViewModels.SearchDeedViewModel
import com.example.routime.Presentation.ViewModels.SettingsViewModel
import com.example.routime.Presentation.ViewModels.ShowDeedViewModel
import com.example.routime.Presentation.ViewModels.TodayDeedViewModel
import com.example.routime.Screen

class ContextViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayDeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayDeedViewModel(context) as T
        }

        if (modelClass.isAssignableFrom(AddNewDeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddNewDeedViewModel(context) as T
        }

        if (modelClass.isAssignableFrom(SearchDeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchDeedViewModel(context) as T
        }

        if (modelClass.isAssignableFrom(ShowDeedViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ShowDeedViewModel(context) as T
        }

        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}